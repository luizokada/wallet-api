package wallet.api.domain.expense.dto;

import wallet.api.domain.expense.entity.Expense;
import wallet.api.domain.expenseCategory.dtos.ExpenseCateogryToApiVIewDTO;

import java.util.Date;

public record ExpenseToApiViewDto(
        String id,
        String description,
        int amount,
        String expenseAt,
        ExpenseCateogryToApiVIewDTO expenseCategory



) {


    public ExpenseToApiViewDto(Expense expense, ExpenseCateogryToApiVIewDTO expenseCategory){

        this(expense.getId(), expense.getDescription(), expense.getAmount(), expense.getExpense_date().toString(), expenseCategory);
    }
}
