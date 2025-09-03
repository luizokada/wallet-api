package wallet.api.domain.expense.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Positive;

public record UpdateExpenseDTO(
        @DateTimeFormat
        Date expenseDate,
        String description,
        @Positive
        Integer amount,
        String categoryId
) {
}
