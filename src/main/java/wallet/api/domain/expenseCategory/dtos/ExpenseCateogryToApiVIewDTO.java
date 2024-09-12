package wallet.api.domain.expenseCategory.dtos;

import wallet.api.domain.expenseCategory.entity.ExpenseCategory;
import wallet.api.domain.user.dtos.UserToApiViewDTO;
import wallet.api.domain.user.entity.User;

import java.util.List;

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
