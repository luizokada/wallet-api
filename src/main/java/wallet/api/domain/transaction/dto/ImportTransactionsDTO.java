package wallet.api.domain.transaction.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ImportTransactionsDTO(
        @NotEmpty
        @Size(max = 500, message = "a batch can have at most 500 transactions")
        List<@Valid ImportTransactionItemDTO> transactions,

        Boolean skipDuplicates
) {

    public boolean shouldSkipDuplicates() {
        return skipDuplicates == null || skipDuplicates;
    }
}
