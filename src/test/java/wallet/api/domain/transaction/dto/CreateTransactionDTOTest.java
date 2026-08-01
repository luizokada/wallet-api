package wallet.api.domain.transaction.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import wallet.api.domain.transaction.entity.TransactionType;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateTransactionDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldRejectMissingTypeMissingDateAndNonPositiveAmount() {
        var violations = validator.validate(new CreateTransactionDTO(null, null, null, 0, null));

        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertEquals(Set.of("type", "date", "amount"), fields);
    }

    @Test
    void shouldRejectNegativeAmount() {
        var violations = validator.validate(
                new CreateTransactionDTO(TransactionType.EXPENSE, new Date(), "x", -100, null));

        assertEquals(1, violations.size());
        assertEquals("amount", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldAcceptValidTransaction() {
        var violations = validator.validate(
                new CreateTransactionDTO(TransactionType.INCOME, new Date(), "Salário", 500000, null));

        assertTrue(violations.isEmpty());
    }
}
