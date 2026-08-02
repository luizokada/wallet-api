package wallet.api.domain.transaction.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import wallet.api.domain.transaction.entity.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportTransactionItemDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private ImportTransactionItemDTO item(Integer installmentNumber, Integer installmentTotal, String seriesId) {
        return new ImportTransactionItemDTO(
                TransactionType.EXPENSE,
                LocalDate.of(2026, 7, 15),
                "IFOOD *RESTAURANTE",
                4590,
                null,
                installmentNumber,
                installmentTotal,
                seriesId
        );
    }

    @Test
    void shouldAcceptItemWithoutInstallments() {
        assertTrue(validator.validate(item(null, null, null)).isEmpty());
    }

    @Test
    void shouldAcceptFullInstallment() {
        assertTrue(validator.validate(item(3, 10, "series-1")).isEmpty());
    }

    @Test
    void shouldRejectInstallmentNumberWithoutTotal() {
        var violations = validator.validate(item(3, null, null));

        assertEquals(1, violations.size());
        assertEquals("installmentPairValid", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldRejectInstallmentNumberGreaterThanTotal() {
        var violations = validator.validate(item(11, 10, "series-1"));

        assertEquals(1, violations.size());
        assertEquals("installmentPairValid", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldRejectInstallmentWithoutSeriesId() {
        var violations = validator.validate(item(3, 10, null));

        assertEquals(1, violations.size());
        assertEquals("seriesValid", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldRejectMissingTypeMissingDateAndNonPositiveAmount() {
        var violations = validator.validate(
                new ImportTransactionItemDTO(null, null, null, 0, null, null, null, null));

        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertEquals(Set.of("type", "date", "amount"), fields);
    }

    @Test
    void shouldRejectEmptyBatch() {
        var violations = validator.validate(new ImportTransactionsDTO(List.of(), null));

        assertEquals(1, violations.size());
        assertEquals("transactions", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldRejectBatchAboveFiveHundredItems() {
        var oversized = java.util.Collections.nCopies(501, item(null, null, null));

        var violations = validator.validate(new ImportTransactionsDTO(oversized, null));

        assertEquals(1, violations.size());
        assertEquals("transactions", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldSkipDuplicatesByDefault() {
        assertTrue(new ImportTransactionsDTO(List.of(item(null, null, null)), null).shouldSkipDuplicates());
    }
}
