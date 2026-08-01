package wallet.api.domain.transaction.dto;

import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import wallet.api.domain.transaction.entity.TransactionType;

import java.util.Date;

public record UpdateTransactionDTO(
        TransactionType type,

        @DateTimeFormat
        Date date,
        String description,
        @Positive
        Integer amount,
        String categoryId
) {
}
