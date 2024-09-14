package wallet.api.domain.expense.service;

import org.springframework.stereotype.Service;
import wallet.api.domain.expense.dto.CreateExpenseDTO;
import wallet.api.domain.expense.entity.Expense;
import wallet.api.domain.expense.repository.ExpenseRepository;
import wallet.api.domain.expenseCategory.entity.ExpenseCategory;
import wallet.api.domain.user.entity.User;
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
        System.out.println(wallet.getId());


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
}
