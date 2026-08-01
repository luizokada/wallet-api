package wallet.api.domain.category.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wallet.api.domain.category.dtos.CreateCategoryDTO;
import wallet.api.domain.category.dtos.UpdateCategoryDTO;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.category.repository.CategoryRepository;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.errors.category.CategoryNotFound;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createShouldPersistWithType() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        var category = categoryService.createCategory(new CreateCategoryDTO("Salário", "Renda mensal", TransactionType.INCOME));

        assertEquals("Salário", category.getName());
        assertEquals(TransactionType.INCOME, category.getType());
    }

    @Test
    void updateShouldChangeNameButNeverType() {
        var existing = new Category("cat-1", "Mercado", "desc", TransactionType.EXPENSE);
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        var updated = categoryService.updateCategory("cat-1", new UpdateCategoryDTO("Supermercado", null));

        assertEquals("Supermercado", updated.getName());
        assertEquals(TransactionType.EXPENSE, updated.getType());
    }

    @Test
    void listShouldFilterByTypeWhenGiven() {
        var income = new Category("cat-1", "Salário", "desc", TransactionType.INCOME);
        when(categoryRepository.findAllByType(TransactionType.INCOME)).thenReturn(List.of(income));

        var result = categoryService.getAllCategories(TransactionType.INCOME);

        assertEquals(List.of(income), result);
        verify(categoryRepository).findAllByType(TransactionType.INCOME);
    }

    @Test
    void listShouldReturnAllWhenTypeIsNull() {
        var categories = List.of(new Category("cat-1", "Salário", "desc", TransactionType.INCOME));
        when(categoryRepository.findAll()).thenReturn(categories);

        assertEquals(categories, categoryService.getAllCategories(null));
    }

    @Test
    void getByIdShouldThrowWhenMissing() {
        when(categoryRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(CategoryNotFound.class, () -> categoryService.getCategoryById("missing"));
    }
}
