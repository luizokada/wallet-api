package wallet.api.domain.expenseCategory.dtos;

import java.util.List;

import wallet.api.domain.expenseCategory.entity.ExpenseCategory;

public record ExpenseCateogryToApiVIewDTO(
        String id,
        String name,
        String description
) {

    public ExpenseCateogryToApiVIewDTO(ExpenseCategory expenseCategory) {
        this(expenseCategory.getId(), expenseCategory.getName(), expenseCategory.getDescription());
    }

    public static List<ExpenseCateogryToApiVIewDTO> toList(List<ExpenseCategory> categories) {
        return categories.stream()
                .map(ExpenseCateogryToApiVIewDTO::new)
                .toList();
    }
}
