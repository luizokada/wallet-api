package wallet.api.domain.category.dtos;

import java.util.List;

import wallet.api.domain.category.entity.Category;
import wallet.api.domain.transaction.entity.TransactionType;

public record CategoryToApiViewDTO(
        String id,
        String name,
        String description,
        TransactionType type,
        boolean isDefault
) {

    public CategoryToApiViewDTO(Category category) {
        this(category.getId(), category.getName(), category.getDescription(), category.getType(), category.isDefault());
    }

    public static List<CategoryToApiViewDTO> toList(List<Category> categories) {
        return categories.stream()
                .map(CategoryToApiViewDTO::new)
                .toList();
    }
}
