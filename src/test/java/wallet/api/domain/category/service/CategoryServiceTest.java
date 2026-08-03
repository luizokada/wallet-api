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
import wallet.api.domain.user.entity.User;
import wallet.api.errors.category.CategoryNotFound;
import wallet.api.errors.category.DefaultCategoryNotEditable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private final User user = new User("user-id", "Test User", "user@example.com", "hash", null, null, null, null, null);

    @Test
    void createShouldPersistWithTypeAndOwner() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        var category = categoryService.createCategory(user, new CreateCategoryDTO("Salário", "Renda mensal", TransactionType.INCOME));

        assertEquals("Salário", category.getName());
        assertEquals(TransactionType.INCOME, category.getType());
        assertEquals(user, category.getOwner());
        assertFalse(category.isDefault());
    }

    @Test
    void updateShouldChangeNameButNeverType() {
        var existing = new Category("cat-1", "Mercado", "desc", TransactionType.EXPENSE, user);
        when(categoryRepository.findVisibleById("user-id", "cat-1")).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        var updated = categoryService.updateCategory(user, "cat-1", new UpdateCategoryDTO("Supermercado", null));

        assertEquals("Supermercado", updated.getName());
        assertEquals(TransactionType.EXPENSE, updated.getType());
    }

    @Test
    void updateShouldRejectDefaultCategory() {
        var defaultCategory = new Category("cat-default", "Alimentação", "desc", TransactionType.EXPENSE);
        when(categoryRepository.findVisibleById("user-id", "cat-default")).thenReturn(Optional.of(defaultCategory));

        assertThrows(DefaultCategoryNotEditable.class,
                () -> categoryService.updateCategory(user, "cat-default", new UpdateCategoryDTO("Outro nome", null)));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowWhenCategoryIsNotVisible() {
        when(categoryRepository.findVisibleById("user-id", "cat-from-other-user")).thenReturn(Optional.empty());

        assertThrows(CategoryNotFound.class,
                () -> categoryService.updateCategory(user, "cat-from-other-user", new UpdateCategoryDTO("Minha", null)));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void listShouldFilterByTypeWhenGiven() {
        var income = new Category("cat-1", "Salário", "desc", TransactionType.INCOME, user);
        when(categoryRepository.findAllVisibleByType("user-id", TransactionType.INCOME)).thenReturn(List.of(income));

        var result = categoryService.getAllCategories(user, TransactionType.INCOME);

        assertEquals(List.of(income), result);
        verify(categoryRepository).findAllVisibleByType("user-id", TransactionType.INCOME);
    }

    @Test
    void listShouldReturnOnlyVisibleWhenTypeIsNull() {
        var categories = List.of(
                new Category("cat-1", "Salário", "desc", TransactionType.INCOME, user),
                new Category("cat-default", "Alimentação", "desc", TransactionType.EXPENSE)
        );
        when(categoryRepository.findAllVisible("user-id")).thenReturn(categories);

        assertEquals(categories, categoryService.getAllCategories(user, null));
        verify(categoryRepository, never()).findAll();
    }

    @Test
    void getByIdShouldThrowWhenNotVisible() {
        when(categoryRepository.findVisibleById("user-id", "missing")).thenReturn(Optional.empty());

        assertThrows(CategoryNotFound.class, () -> categoryService.getCategoryById(user, "missing"));
    }

    @Test
    void getByIdShouldReturnDefaultCategory() {
        var defaultCategory = new Category("cat-default", "Alimentação", "desc", TransactionType.EXPENSE);
        when(categoryRepository.findVisibleById("user-id", "cat-default")).thenReturn(Optional.of(defaultCategory));

        assertTrue(categoryService.getCategoryById(user, "cat-default").isDefault());
    }
}
