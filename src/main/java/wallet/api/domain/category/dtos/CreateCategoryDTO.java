package wallet.api.domain.category.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import wallet.api.domain.transaction.entity.TransactionType;

public record CreateCategoryDTO(
        @NotBlank
        @NotEmpty
        String name,

        @NotBlank
        @NotEmpty
        String description,

        @NotNull
        TransactionType type
) {
}
