package wallet.api.domain.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateWalletDTO(
        @Positive
        @NotNull
        Integer balance
) {
}
