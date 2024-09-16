package wallet.api.domain.expense.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record UpdateExpenseDTO(
        @DateTimeFormat
        Date expenseDate,
        String description,
        @Positive
        Integer amount,
        String categoryId
) {
}
