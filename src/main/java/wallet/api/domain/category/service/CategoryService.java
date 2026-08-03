package wallet.api.domain.category.service;

import org.springframework.stereotype.Service;
import wallet.api.domain.category.dtos.CreateCategoryDTO;
import wallet.api.domain.category.dtos.UpdateCategoryDTO;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.category.repository.CategoryRepository;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.domain.user.entity.User;
import wallet.api.errors.category.CategoryNotFound;
import wallet.api.errors.category.DefaultCategoryNotEditable;

import java.util.List;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(User currentUser, CreateCategoryDTO body) {
        var category = new Category(body, currentUser);
        return categoryRepository.save(category);
    }

    public Category getCategoryById(User currentUser, String id) {
        return findVisible(currentUser, id);
    }

    public List<Category> getAllCategories(User currentUser, TransactionType type) {
        if (type == null) {
            return categoryRepository.findAllVisible(currentUser.getId());
        }
        return categoryRepository.findAllVisibleByType(currentUser.getId(), type);
    }

    public Category updateCategory(User currentUser, String id, UpdateCategoryDTO body) {
        var found = findVisible(currentUser, id);

        if (found.isDefault()) {
            throw new DefaultCategoryNotEditable();
        }

        found.update(body);
        return categoryRepository.save(found);
    }

    private Category findVisible(User currentUser, String id) {
        return categoryRepository.findVisibleById(currentUser.getId(), id)
                .orElseThrow(CategoryNotFound::new);
    }


}
