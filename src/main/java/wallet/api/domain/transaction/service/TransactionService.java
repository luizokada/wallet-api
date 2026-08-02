package wallet.api.domain.transaction.service;

import org.springframework.stereotype.Service;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.category.repository.CategoryRepository;
import wallet.api.domain.transaction.dto.CreateTransactionDTO;
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
            category = categoryRepository.findById(payload.categoryId()).orElse(null);

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
            newCategory = categoryRepository.findById(payload.categoryId()).orElse(null);

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

}
