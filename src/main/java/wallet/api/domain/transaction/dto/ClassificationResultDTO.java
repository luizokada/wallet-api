package wallet.api.domain.transaction.dto;

import java.util.List;

public record ClassificationResultDTO(
        int classified,
        int unmatched,
        List<ClassifiedItemDTO> items
) {

    public record ClassifiedItemDTO(
            int index,
            String categoryId,
            String categoryName,
            double confidence
    ) {
    }
}
