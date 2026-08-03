package wallet.api.domain.transaction.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.category.repository.CategoryRepository;
import wallet.api.domain.transaction.dto.CreateTransactionDTO;
import wallet.api.domain.transaction.dto.ImportTransactionItemDTO;
import wallet.api.domain.transaction.dto.ImportTransactionsDTO;
import wallet.api.domain.transaction.dto.UpdateTransactionDTO;
import wallet.api.domain.transaction.entity.Transaction;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.domain.transaction.repository.TransactionRepository;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.wallet.entity.Wallet;
import wallet.api.errors.transaction.CategoryTypeMismatchError;
import wallet.api.errors.transaction.NoWalletFound;
import wallet.api.errors.transaction.NotTransactionOwnerError;
import wallet.api.errors.category.CategoryNotFound;
import wallet.api.errors.transaction.TransactionNotFound;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    private final User user = new User("user-id", "Test User", "user@example.com", "hash", null, null, null, null, null);
    private final Wallet wallet = new Wallet(user);
    private final Category incomeCategory = new Category("cat-income", "Salário", "desc", TransactionType.INCOME);
    private final Category expenseCategory = new Category("cat-expense", "Mercado", "desc", TransactionType.EXPENSE);

    @Test
    void createShouldSaveIncomeWithMatchingCategory() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(categoryRepository.findVisibleById("user-id", "cat-income")).thenReturn(Optional.of(incomeCategory));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        var dto = new CreateTransactionDTO(TransactionType.INCOME, new Date(), "Salário do mês", 500000, "cat-income");
        var transaction = transactionService.createTransaction(user, dto);

        assertEquals(TransactionType.INCOME, transaction.getType());
        assertEquals(500000, transaction.getAmount());
        assertEquals(incomeCategory, transaction.getCategory());
    }

    @Test
    void createShouldRejectCategoryOfDifferentType() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(categoryRepository.findVisibleById("user-id", "cat-expense")).thenReturn(Optional.of(expenseCategory));

        var dto = new CreateTransactionDTO(TransactionType.INCOME, new Date(), "x", 100, "cat-expense");

        assertThrows(CategoryTypeMismatchError.class, () -> transactionService.createTransaction(user, dto));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createShouldRejectCategoryOfAnotherUser() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(categoryRepository.findVisibleById("user-id", "cat-from-other-user")).thenReturn(Optional.empty());

        var dto = new CreateTransactionDTO(TransactionType.EXPENSE, new Date(), "x", 100, "cat-from-other-user");

        assertThrows(CategoryNotFound.class, () -> transactionService.createTransaction(user, dto));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenUserHasNoWallet() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(null);

        var dto = new CreateTransactionDTO(TransactionType.EXPENSE, new Date(), "x", 100, null);

        assertThrows(NoWalletFound.class, () -> transactionService.createTransaction(user, dto));
    }

    @Test
    void createWithoutCategoryShouldSkipCategoryValidation() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        var dto = new CreateTransactionDTO(TransactionType.EXPENSE, new Date(), "sem categoria", 2000, null);
        var transaction = transactionService.createTransaction(user, dto);

        assertEquals(TransactionType.EXPENSE, transaction.getType());
        verify(categoryRepository, never()).findVisibleById(any(), any());
    }

    @Test
    void updateShouldRejectNewTypeIncompatibleWithExistingCategory() {
        var existing = new Transaction(wallet, expenseCategory,
                new CreateTransactionDTO(TransactionType.EXPENSE, new Date(), "mercado", 3000, "cat-expense"));
        when(transactionRepository.findTransactionById("t-1")).thenReturn(existing);
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);

        var payload = new UpdateTransactionDTO(TransactionType.INCOME, null, null, null, null);

        assertThrows(CategoryTypeMismatchError.class,
                () -> transactionService.updateTransaction("t-1", user, payload));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void updateShouldAcceptTypeAndCategoryChangedTogether() {
        var existing = new Transaction(wallet, expenseCategory,
                new CreateTransactionDTO(TransactionType.EXPENSE, new Date(), "mercado", 3000, "cat-expense"));
        when(transactionRepository.findTransactionById("t-1")).thenReturn(existing);
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(categoryRepository.findVisibleById("user-id", "cat-income")).thenReturn(Optional.of(incomeCategory));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        var payload = new UpdateTransactionDTO(TransactionType.INCOME, null, null, null, "cat-income");
        var updated = transactionService.updateTransaction("t-1", user, payload);

        assertEquals(TransactionType.INCOME, updated.getType());
        assertEquals(incomeCategory, updated.getCategory());
    }

    @Test
    void updateShouldRejectCategoryOfAnotherUser() {
        var existing = new Transaction(wallet, expenseCategory,
                new CreateTransactionDTO(TransactionType.EXPENSE, new Date(), "mercado", 3000, "cat-expense"));
        when(transactionRepository.findTransactionById("t-1")).thenReturn(existing);
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(categoryRepository.findVisibleById("user-id", "cat-from-other-user")).thenReturn(Optional.empty());

        var payload = new UpdateTransactionDTO(null, null, null, null, "cat-from-other-user");

        assertThrows(CategoryNotFound.class,
                () -> transactionService.updateTransaction("t-1", user, payload));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deleteShouldRemoveTransactionWhenUserIsOwner() {
        var existing = new Transaction(wallet, null,
                new CreateTransactionDTO(TransactionType.EXPENSE, new Date(), "mercado", 3000, null));
        when(transactionRepository.findTransactionById("t-1")).thenReturn(existing);

        transactionService.deleteTransaction("t-1", user);

        verify(transactionRepository).delete(existing);
    }

    @Test
    void deleteShouldRejectWhenUserIsNotOwner() {
        var existing = new Transaction(wallet, null,
                new CreateTransactionDTO(TransactionType.EXPENSE, new Date(), "mercado", 3000, null));
        when(transactionRepository.findTransactionById("t-1")).thenReturn(existing);
        var otherUser = new User("other-id", "Other User", "other@example.com", "hash", null, null, null, null, null);

        assertThrows(NotTransactionOwnerError.class,
                () -> transactionService.deleteTransaction("t-1", otherUser));

        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void deleteShouldThrowWhenTransactionNotFound() {
        when(transactionRepository.findTransactionById("missing")).thenReturn(null);

        assertThrows(TransactionNotFound.class,
                () -> transactionService.deleteTransaction("missing", user));
    }

    private ImportTransactionItemDTO importItem(String description, int amount, String categoryId) {
        return new ImportTransactionItemDTO(TransactionType.EXPENSE, LocalDate.of(2026, 7, 15),
                description, amount, categoryId, null, null, null);
    }

    private ImportTransactionItemDTO installmentItem(int number, int total, String seriesId) {
        return new ImportTransactionItemDTO(TransactionType.EXPENSE, LocalDate.of(2026, 7, 15),
                "Netshoes", 12990, null, number, total, seriesId);
    }

    private void givenWalletWithoutTransactions() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(transactionRepository.findByWalletIdAndDateBetween(any(), any(), any())).thenReturn(List.of());
        when(transactionRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void importShouldCreateEveryItemWhenThereAreNoDuplicates() {
        givenWalletWithoutTransactions();

        var payload = new ImportTransactionsDTO(List.of(
                importItem("IFOOD", 4590, null),
                importItem("UBER", 2100, null),
                importItem("NETFLIX", 5590, null)
        ), null);

        var result = transactionService.importTransactions(user, payload);

        assertEquals(3, result.created());
        assertEquals(0, result.skipped());
        assertEquals(3, result.transactions().size());
    }

    @Test
    void importShouldThrowWhenUserHasNoWallet() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(null);

        var payload = new ImportTransactionsDTO(List.of(importItem("IFOOD", 4590, null)), null);

        assertThrows(NoWalletFound.class, () -> transactionService.importTransactions(user, payload));

        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void importShouldThrowWhenCategoryDoesNotExist() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(categoryRepository.findAllVisibleByIdIn(anyString(), anyList())).thenReturn(List.of());

        var payload = new ImportTransactionsDTO(List.of(importItem("IFOOD", 4590, "missing-cat")), null);

        assertThrows(CategoryNotFound.class, () -> transactionService.importTransactions(user, payload));

        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void importShouldThrowWhenCategoryBelongsToAnotherUser() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(categoryRepository.findAllVisibleByIdIn(anyString(), anyList())).thenReturn(List.of());

        var payload = new ImportTransactionsDTO(List.of(importItem("IFOOD", 4590, "cat-from-other-user")), null);

        assertThrows(CategoryNotFound.class, () -> transactionService.importTransactions(user, payload));

        verify(categoryRepository).findAllVisibleByIdIn(eq("user-id"), anyList());
        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void importShouldRejectCategoryOfDifferentType() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(categoryRepository.findAllVisibleByIdIn(anyString(), anyList())).thenReturn(List.of(incomeCategory));
        when(transactionRepository.findByWalletIdAndDateBetween(any(), any(), any())).thenReturn(List.of());

        var payload = new ImportTransactionsDTO(List.of(importItem("IFOOD", 4590, "cat-income")), null);

        assertThrows(CategoryTypeMismatchError.class, () -> transactionService.importTransactions(user, payload));

        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void importShouldLoadCategoriesInASingleQuery() {
        givenWalletWithoutTransactions();
        when(categoryRepository.findAllVisibleByIdIn(anyString(), anyList())).thenReturn(List.of(expenseCategory));

        var payload = new ImportTransactionsDTO(List.of(
                importItem("MERCADO A", 4590, "cat-expense"),
                importItem("MERCADO B", 7100, "cat-expense")
        ), null);

        transactionService.importTransactions(user, payload);

        verify(categoryRepository, times(1)).findAllVisibleByIdIn(eq("user-id"), anyList());
        verify(categoryRepository, never()).findVisibleById(any(), any());
    }

    @Test
    void importShouldSkipItemAlreadyPresentInTheWallet() {
        var alreadySaved = new Transaction(wallet, null, importItem("IFOOD", 4590, null));
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(transactionRepository.findByWalletIdAndDateBetween(any(), any(), any()))
                .thenReturn(List.of(alreadySaved));
        when(transactionRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        var payload = new ImportTransactionsDTO(List.of(
                importItem("IFOOD", 4590, null),
                importItem("UBER", 2100, null)
        ), null);

        var result = transactionService.importTransactions(user, payload);

        assertEquals(1, result.created());
        assertEquals(1, result.skipped());
        assertEquals("IFOOD", result.duplicates().getFirst().description());
    }

    @Test
    void importShouldSkipDuplicateInsideTheSameBatch() {
        givenWalletWithoutTransactions();

        var payload = new ImportTransactionsDTO(List.of(
                importItem("IFOOD", 4590, null),
                importItem("IFOOD", 4590, null)
        ), null);

        var result = transactionService.importTransactions(user, payload);

        assertEquals(1, result.created());
        assertEquals(1, result.skipped());
    }

    @Test
    void importShouldTreatDifferentInstallmentNumbersAsDistinctTransactions() {
        givenWalletWithoutTransactions();

        var payload = new ImportTransactionsDTO(List.of(
                installmentItem(3, 10, "series-1"),
                installmentItem(4, 10, "series-1")
        ), null);

        var result = transactionService.importTransactions(user, payload);

        assertEquals(2, result.created());
        assertEquals(0, result.skipped());
    }

    @Test
    void importShouldIgnoreSeriesIdWhenDetectingDuplicates() {
        var projectedLastMonth = new Transaction(wallet, null, installmentItem(4, 10, "series-from-july"));
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(transactionRepository.findByWalletIdAndDateBetween(any(), any(), any()))
                .thenReturn(List.of(projectedLastMonth));
        when(transactionRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        var payload = new ImportTransactionsDTO(List.of(installmentItem(4, 10, "series-from-august")), null);

        var result = transactionService.importTransactions(user, payload);

        assertEquals(0, result.created());
        assertEquals(1, result.skipped());
    }

    @Test
    void importShouldCreateDuplicatesWhenSkipIsDisabled() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(transactionRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        var payload = new ImportTransactionsDTO(List.of(
                importItem("IFOOD", 4590, null),
                importItem("IFOOD", 4590, null)
        ), false);

        var result = transactionService.importTransactions(user, payload);

        assertEquals(2, result.created());
        assertEquals(0, result.skipped());
        verify(transactionRepository, never()).findByWalletIdAndDateBetween(any(), any(), any());
    }

    @Test
    void listShouldOnlyReadTheWalletOfTheAuthenticatedUser() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(transactionRepository.findPageByWalletId(eq(wallet.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        transactionService.listByUser(user, 0, 50);

        verify(transactionRepository).findPageByWalletId(eq(wallet.getId()), any(Pageable.class));
    }

    @Test
    void listShouldForwardPageAndSizeToTheRepository() {
        var pageable = ArgumentCaptor.forClass(Pageable.class);
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(transactionRepository.findPageByWalletId(any(), pageable.capture()))
                .thenReturn(new PageImpl<>(List.of()));

        transactionService.listByUser(user, 3, 25);

        assertEquals(3, pageable.getValue().getPageNumber());
        assertEquals(25, pageable.getValue().getPageSize());
    }

    @Test
    void listShouldThrowWhenUserHasNoWallet() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(null);

        assertThrows(NoWalletFound.class, () -> transactionService.listByUser(user, 0, 50));

        verify(transactionRepository, never()).findPageByWalletId(any(), any());
    }
}
