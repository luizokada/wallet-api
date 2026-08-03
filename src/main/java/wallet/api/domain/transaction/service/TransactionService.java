package wallet.api.domain.transaction.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.category.repository.CategoryRepository;
import wallet.api.domain.transaction.dto.CreateTransactionDTO;
import wallet.api.domain.transaction.dto.ImportResultDTO;
import wallet.api.domain.transaction.dto.ImportTransactionItemDTO;
import wallet.api.domain.transaction.dto.ImportTransactionsDTO;
import wallet.api.domain.transaction.dto.TransactionToApiViewDTO;
import wallet.api.domain.transaction.dto.UpdateTransactionDTO;
import wallet.api.domain.transaction.entity.Transaction;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.domain.transaction.repository.TransactionRepository;
import wallet.api.domain.user.entity.User;
import wallet.api.errors.category.CategoryNotFound;
import wallet.api.errors.transaction.CategoryTypeMismatchError;
import wallet.api.errors.transaction.NoWalletFound;
import wallet.api.errors.transaction.NotTransactionOwnerError;
import wallet.api.errors.transaction.TransactionNotFound;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionService {


    private final TransactionRepository transactionRepository;

    private final CategoryRepository categoryRepository;


    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public Transaction createTransaction(User currentUser, CreateTransactionDTO payload) {
        var wallet = transactionRepository.findWalletByUserId(currentUser.getId());
        if (wallet == null) {
            throw new NoWalletFound();
        }

        Category category = null;
        if (payload.categoryId() != null) {
            category = categoryRepository.findVisibleById(currentUser.getId(), payload.categoryId()).orElse(null);

            if (category == null) {
                throw new CategoryNotFound();
            }
            if (category.getType() != payload.type()) {
                throw new CategoryTypeMismatchError();
            }
        }

        var transaction = new Transaction(wallet, category, payload);

        return transactionRepository.save(transaction);
    }

    public ImportResultDTO importTransactions(User currentUser, ImportTransactionsDTO payload) {
        var wallet = transactionRepository.findWalletByUserId(currentUser.getId());
        if (wallet == null) {
            throw new NoWalletFound();
        }

        var categoriesById = loadCategories(currentUser, payload.transactions());
        var skipDuplicates = payload.shouldSkipDuplicates();
        var knownKeys = skipDuplicates
                ? loadExistingKeys(wallet.getId(), payload.transactions())
                : new HashSet<String>();

        var toCreate = new ArrayList<Transaction>();
        var duplicates = new ArrayList<ImportResultDTO.DuplicateItemDTO>();

        for (var item : payload.transactions()) {
            var category = resolveCategory(item, categoriesById);

            if (skipDuplicates && !knownKeys.add(dedupeKey(item))) {
                duplicates.add(new ImportResultDTO.DuplicateItemDTO(item.date(), item.description(), item.amount()));
                continue;
            }

            toCreate.add(new Transaction(wallet, category, item));
        }

        var created = transactionRepository.saveAll(toCreate);

        return new ImportResultDTO(
                created.size(),
                duplicates.size(),
                TransactionToApiViewDTO.fromList(created),
                duplicates
        );
    }

    private Map<String, Category> loadCategories(User currentUser, List<ImportTransactionItemDTO> items) {
        var ids = items.stream()
                .map(ImportTransactionItemDTO::categoryId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();

        if (ids.isEmpty()) {
            return Map.of();
        }

        var found = categoryRepository.findAllVisibleByIdIn(currentUser.getId(), ids).stream()
                .collect(Collectors.toMap(Category::getId, category -> category));

        if (found.size() != ids.size()) {
            throw new CategoryNotFound();
        }

        return found;
    }

    private Category resolveCategory(ImportTransactionItemDTO item, Map<String, Category> categoriesById) {
        if (item.categoryId() == null || item.categoryId().isBlank()) {
            return null;
        }

        var category = categoriesById.get(item.categoryId());
        if (category == null) {
            throw new CategoryNotFound();
        }
        if (category.getType() != item.type()) {
            throw new CategoryTypeMismatchError();
        }

        return category;
    }

    private Set<String> loadExistingKeys(String walletId, List<ImportTransactionItemDTO> items) {
        var dates = items.stream().map(ImportTransactionItemDTO::date).sorted().toList();
        var existing = transactionRepository.findByWalletIdAndDateBetween(
                walletId,
                toDate(dates.getFirst()),
                toDate(dates.getLast())
        );

        return existing.stream().map(this::dedupeKey).collect(Collectors.toCollection(HashSet::new));
    }

    private String dedupeKey(ImportTransactionItemDTO item) {
        return dedupeKey(item.type(), item.date(), item.amount(), item.description(), item.installmentNumber());
    }

    private String dedupeKey(Transaction transaction) {
        return dedupeKey(
                transaction.getType(),
                toLocalDate(transaction.getDate()),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getInstallmentNumber()
        );
    }

    private String dedupeKey(TransactionType type, LocalDate date, int amount, String description, Integer installmentNumber) {
        return String.join("|",
                type.name(),
                date.toString(),
                String.valueOf(amount),
                normalizeDescription(description),
                installmentNumber == null ? "-" : installmentNumber.toString()
        );
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return "";
        }
        return description.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Transaction updateTransaction(String id, User currentUser, UpdateTransactionDTO payload) {

        Transaction transaction = transactionRepository.findTransactionById(id);

        if (transaction == null) {
            throw new TransactionNotFound();
        }
        var wallet = transactionRepository.findWalletByUserId(currentUser.getId());
        if (wallet == null) {
            throw new NoWalletFound();
        }
        Category newCategory = null;
        if (payload.categoryId() != null && !payload.categoryId().isEmpty()) {
            newCategory = categoryRepository.findVisibleById(currentUser.getId(), payload.categoryId()).orElse(null);

            if (newCategory == null) {
                throw new CategoryNotFound();
            }
        }

        // valida o par (tipo, categoria) EFETIVO após aplicar o payload:
        // cobre mudar só o type, só a categoria, ou os dois juntos
        TransactionType effectiveType = payload.type() != null ? payload.type() : transaction.getType();
        Category effectiveCategory = newCategory != null ? newCategory : transaction.getCategory();
        if (effectiveCategory != null && effectiveCategory.getType() != effectiveType) {
            throw new CategoryTypeMismatchError();
        }

        transaction.update(payload, newCategory);

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(String id, User currentUser) {
        Transaction transaction = transactionRepository.findTransactionById(id);

        if (transaction == null) {
            throw new TransactionNotFound();
        }

        if (!transaction.getWallet().getUser().getId().equals(currentUser.getId())) {
            throw new NotTransactionOwnerError();
        }

        transactionRepository.delete(transaction);
    }

    public Transaction getTransaction(String id) {
        Transaction transaction = transactionRepository.findTransactionById(id);

        if (transaction == null) {
            throw new TransactionNotFound();
        }

        return transaction;
    }

    public Page<Transaction> listByUser(User currentUser, int page, int size) {
        var wallet = transactionRepository.findWalletByUserId(currentUser.getId());

        if (wallet == null) {
            throw new NoWalletFound();
        }

        return transactionRepository.findPageByWalletId(wallet.getId(), PageRequest.of(page, size));
    }

}
