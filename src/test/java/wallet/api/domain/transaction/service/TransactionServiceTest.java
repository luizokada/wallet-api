package wallet.api.domain.transaction.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.category.repository.CategoryRepository;
import wallet.api.domain.transaction.dto.CreateTransactionDTO;
import wallet.api.domain.transaction.dto.UpdateTransactionDTO;
import wallet.api.domain.transaction.entity.Transaction;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.domain.transaction.repository.TransactionRepository;
import wallet.api.domain.user.entity.User;
import wallet.api.domain.wallet.entity.Wallet;
import wallet.api.errors.transaction.CategoryTypeMismatchError;
import wallet.api.errors.transaction.NoWalletFound;
import wallet.api.errors.transaction.NotTransactionOwnerError;
import wallet.api.errors.transaction.TransactionNotFound;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

    private final User user = new User("user-id", "Test User", "user@example.com", "hash", null, null, null, null);
    private final Wallet wallet = new Wallet(user);
    private final Category incomeCategory = new Category("cat-income", "Salário", "desc", TransactionType.INCOME);
    private final Category expenseCategory = new Category("cat-expense", "Mercado", "desc", TransactionType.EXPENSE);

    @Test
    void createShouldSaveIncomeWithMatchingCategory() {
        when(transactionRepository.findWalletByUserId("user-id")).thenReturn(wallet);
        when(categoryRepository.findById("cat-income")).thenReturn(Optional.of(incomeCategory));
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
        when(categoryRepository.findById("cat-expense")).thenReturn(Optional.of(expenseCategory));

        var dto = new CreateTransactionDTO(TransactionType.INCOME, new Date(), "x", 100, "cat-expense");

        assertThrows(CategoryTypeMismatchError.class, () -> transactionService.createTransaction(user, dto));

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
        verify(categoryRepository, never()).findById(any());
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
        when(categoryRepository.findById("cat-income")).thenReturn(Optional.of(incomeCategory));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        var payload = new UpdateTransactionDTO(TransactionType.INCOME, null, null, null, "cat-income");
        var updated = transactionService.updateTransaction("t-1", user, payload);

        assertEquals(TransactionType.INCOME, updated.getType());
        assertEquals(incomeCategory, updated.getCategory());
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
        var otherUser = new User("other-id", "Other User", "other@example.com", "hash", null, null, null, null);

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
}
