package wallet.api.domain.expenseCategory.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ExpenseCateogryBodyDTO(
        @NotBlank
        @NotEmpty
        String name,

        @NotBlank
        @NotEmpty
        String description
) {
}
