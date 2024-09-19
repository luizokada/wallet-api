package wallet.api.domain.expense.dto;

import wallet.api.domain.expense.entity.Expense;
import wallet.api.domain.expenseCategory.dtos.ExpenseCateogryToApiVIewDTO;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record ExpenseToApiViewDto(
        String id,
        String description,
        int amount,
        String expenseAt,
        ExpenseCateogryToApiVIewDTO expenseCategory



) {

    public static List<ExpenseToApiViewDto> fromList(List<Expense> expenses){
        return expenses.stream().map(ex->{
            ExpenseCateogryToApiVIewDTO category = null;
            if(ex.getCategory() != null){
                category = new ExpenseCateogryToApiVIewDTO(ex.getCategory());
            }
            return new ExpenseToApiViewDto(ex, category);
        }).toList();

    }


    public ExpenseToApiViewDto(Expense expense, ExpenseCateogryToApiVIewDTO expenseCategory){

        this(expense.getId(), expense.getDescription(), expense.getAmount(), expense.getExpense_date().toGMTString(), expenseCategory);
    }
}
