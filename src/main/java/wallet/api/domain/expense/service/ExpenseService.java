package wallet.api.domain.expense.service;

import org.springframework.stereotype.Service;
import wallet.api.domain.expense.dto.CreateExpenseDTO;
import wallet.api.domain.expense.dto.UpdateExpenseDTO;
import wallet.api.domain.expense.entity.Expense;
import wallet.api.domain.expense.repository.ExpenseRepository;
import wallet.api.domain.expenseCategory.entity.ExpenseCategory;
import wallet.api.domain.user.entity.User;
import wallet.api.errors.expense.ExpenseNotFound;
import wallet.api.errors.expense.NoWalletFound;
import wallet.api.errors.expenseCategory.ExpenseCategoryNotFound;

@Service
public class ExpenseService {


    private final ExpenseRepository expenseRepository;


    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense createExpense(User currentUser, CreateExpenseDTO payload) {
        var wallet = expenseRepository.findWalletByUserId(currentUser.getId());
        if (wallet == null) {
            throw new NoWalletFound();
        }

        ExpenseCategory expenseCategory = null;
        if(payload.categoryId()!=null){
            expenseCategory = expenseRepository.findExpenseCategoryById(payload.categoryId());

            if(expenseCategory==null){
                throw new ExpenseCategoryNotFound();
            }
        }

        var expense = new Expense(wallet, expenseCategory, payload);

        return expenseRepository.save(expense);
    }

    public Expense updateExpense(String id,User currentUser, UpdateExpenseDTO payload) {

        Expense expense = expenseRepository.findExpenseById(id);

        if(expense==null){
            throw new ExpenseNotFound();
        }
        var wallet = expenseRepository.findWalletByUserId(currentUser.getId());
        if (wallet == null) {
            throw new NoWalletFound();
        }
        ExpenseCategory expenseCategory = null;
        if(payload.categoryId()!=null&&!payload.categoryId().isEmpty()){
            expenseCategory = expenseRepository.findExpenseCategoryById(payload.categoryId());

            if(expenseCategory==null){
                throw new ExpenseCategoryNotFound();
            }
        }


        expense.update(payload,expenseCategory);

        return expenseRepository.save(expense);
    }

    public void deleteExpense(String id) {
        Expense expense = expenseRepository.findExpenseById(id);

        if(expense==null){
            throw new ExpenseNotFound();
        }

        expenseRepository.delete(expense);
    }

    public Expense getExpense(String id) {
        Expense expense = expenseRepository.findExpenseById(id);

        if(expense==null){
            throw new ExpenseNotFound();
        }

        return expense;
    }

}
