package wallet.api.domain.transaction.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import wallet.api.domain.transaction.entity.TransactionType;

import java.util.List;

public record ClassifyTransactionsDTO(
        @NotEmpty
        @Size(max = 300, message = "a classification request can have at most 300 items")
        @Valid
        List<ClassifyItemDTO> items,

        List<String> categoryIds
) {

    public record ClassifyItemDTO(
            @NotNull
            Integer index,

            @NotNull
            TransactionType type,

            @Size(max = 255)
            String description
    ) {
    }
}
