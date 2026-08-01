package wallet.api.domain.transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import wallet.api.domain.transaction.entity.TransactionType;

import java.util.Date;

public record CreateTransactionDTO(
        @NotNull
        TransactionType type,

        @NotNull
        @DateTimeFormat
        Date date,

        String description,
        @Positive
        int amount,
        String categoryId
) {
}
