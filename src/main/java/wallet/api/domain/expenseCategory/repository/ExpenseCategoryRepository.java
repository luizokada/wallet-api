package wallet.api.domain.expenseCategory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wallet.api.domain.expenseCategory.entity.ExpenseCategory;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory,String> {

}
