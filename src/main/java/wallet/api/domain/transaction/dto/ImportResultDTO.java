package wallet.api.domain.transaction.dto;

import java.time.LocalDate;
import java.util.List;

public record ImportResultDTO(
        int created,
        int skipped,
        List<TransactionToApiViewDTO> transactions,
        List<DuplicateItemDTO> duplicates
) {

    public record DuplicateItemDTO(LocalDate date, String description, int amount) {
    }
}
