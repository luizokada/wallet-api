package wallet.api.domain.category.dtos;

// Sem campo `type` de propósito: o tipo da categoria é imutável (protege o histórico de transações)
public record UpdateCategoryDTO(
        String name,
        String description
) {
}
