package wallet.api.domain.expense.dto;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record CreateExpenseDTO(
        @NotNull
        @DateTimeFormat
        Date expenseDate,

        String description,
        @Positive
        int amount,
        String categoryId
        ) {
}
