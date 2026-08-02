package wallet.api.domain.transaction.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import wallet.api.domain.transaction.entity.TransactionType;

import java.time.LocalDate;

public record ImportTransactionItemDTO(
        @NotNull
        TransactionType type,

        @NotNull
        LocalDate date,

        @Size(max = 255)
        String description,

        @Positive
        int amount,

        String categoryId,

        @Min(1) @Max(99)
        Integer installmentNumber,

        @Min(1) @Max(99)
        Integer installmentTotal,

        @Size(max = 255)
        String seriesId
) {

    @AssertTrue(message = "installmentNumber and installmentTotal must come together, with number not greater than total")
    public boolean isInstallmentPairValid() {
        if (installmentNumber == null && installmentTotal == null) {
            return true;
        }
        if (installmentNumber == null || installmentTotal == null) {
            return false;
        }
        return installmentNumber <= installmentTotal;
    }

    @AssertTrue(message = "seriesId is required when installmentTotal is greater than 1")
    public boolean isSeriesValid() {
        if (installmentTotal == null || installmentTotal <= 1) {
            return true;
        }
        return seriesId != null && !seriesId.isBlank();
    }
}
