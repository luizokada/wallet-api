package wallet.api.domain.expense.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wallet.api.domain.expense.entity.Expense;
import wallet.api.domain.expenseCategory.entity.ExpenseCategory;
import wallet.api.domain.wallet.entity.Wallet;

public interface ExpenseRepository extends JpaRepository<Expense,String> {

    @Query(value = "SELECT wa FROM wallet wa WHERE wa.userId = ?1" )
    Wallet findWalletByUserId(String userId);

    @Query(value = "SELECT ec FROM expenseCategory ec WHERE ec.id = ?1")
    ExpenseCategory findExpenseCategoryById(String id);

}
