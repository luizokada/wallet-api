package wallet.api.domain.expenseCategory.service;

import org.springframework.stereotype.Service;
import wallet.api.domain.expenseCategory.dtos.ExpenseCateogryBodyDTO;
import wallet.api.domain.expenseCategory.dtos.UpdateExpenseCategoryDTO;
import wallet.api.domain.expenseCategory.entity.ExpenseCategory;
import wallet.api.domain.expenseCategory.repository.ExpenseCategoryRepository;
import wallet.api.errors.expenseCategory.ExpenseCategoryNotFound;

import java.util.List;

@Service
public class ExpenseCategoryService {


    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseCategoryService(ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public ExpenseCategory createExpenseCategory(ExpenseCateogryBodyDTO body) {
        var expenseCategory = new ExpenseCategory(body);
        return expenseCategoryRepository.save(expenseCategory);
    }

    public ExpenseCategory getExpenseCategoryById(String id) {
        var found = expenseCategoryRepository.findById(id).orElse(null);
        if (found==null) {
            throw new ExpenseCategoryNotFound();
        }
        return found;
    }

    public List<ExpenseCategory> getAllExpenseCategories() {
        return expenseCategoryRepository.findAll();
    }

    public ExpenseCategory updateExpenseCategory(String id, UpdateExpenseCategoryDTO body) {
        var found = expenseCategoryRepository.findById(id).orElse(null);
        if (found==null) {
            throw new ExpenseCategoryNotFound();
        }
        found.update(body);
        return expenseCategoryRepository.save(found);
    }


}
