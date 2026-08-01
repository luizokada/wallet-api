package wallet.api.domain.category.service;

import org.springframework.stereotype.Service;
import wallet.api.domain.category.dtos.CreateCategoryDTO;
import wallet.api.domain.category.dtos.UpdateCategoryDTO;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.category.repository.CategoryRepository;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.errors.category.CategoryNotFound;

import java.util.List;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(CreateCategoryDTO body) {
        var category = new Category(body);
        return categoryRepository.save(category);
    }

    public Category getCategoryById(String id) {
        var found = categoryRepository.findById(id).orElse(null);
        if (found == null) {
            throw new CategoryNotFound();
        }
        return found;
    }

    public List<Category> getAllCategories(TransactionType type) {
        if (type == null) {
            return categoryRepository.findAll();
        }
        return categoryRepository.findAllByType(type);
    }

    public Category updateCategory(String id, UpdateCategoryDTO body) {
        var found = categoryRepository.findById(id).orElse(null);
        if (found == null) {
            throw new CategoryNotFound();
        }
        found.update(body);
        return categoryRepository.save(found);
    }


}
