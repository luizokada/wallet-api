package wallet.api.domain.transaction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.category.repository.CategoryRepository;
import wallet.api.domain.transaction.dto.ClassifyTransactionsDTO;
import wallet.api.domain.transaction.dto.ClassifyTransactionsDTO.ClassifyItemDTO;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.domain.user.entity.User;
import wallet.api.errors.ai.ClassificationFailedError;
import wallet.api.errors.ai.ClassificationRateLimitedError;
import wallet.api.errors.category.CategoryNotFound;
import wallet.api.infra.ai.ClassificationRateLimiter;
import wallet.api.infra.ai.GeminiClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionClassificationServiceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private GeminiClient geminiClient;

    @Mock
    private ClassificationRateLimiter rateLimiter;

    @InjectMocks
    private TransactionClassificationService service;

    private final User user = new User("user-id", "Test User", "user@example.com", "hash", null, null, null, null, null);
    private final Category food = new Category("cat-food", "Comida", "desc", TransactionType.EXPENSE);
    private final Category transport = new Category("cat-transport", "Transporte", "desc", TransactionType.EXPENSE);
    private final Category salary = new Category("cat-salary", "Salário", "desc", TransactionType.INCOME);

    @BeforeEach
    void setMinConfidence() {
        ReflectionTestUtils.setField(service, "minConfidence", 0.7);
    }

    private ClassifyItemDTO item(int index, String description, TransactionType type) {
        return new ClassifyItemDTO(index, type, description);
    }

    private void geminiAnswers(String json) {
        try {
            when(geminiClient.generateJson(anyString(), anyString(), any())).thenReturn(MAPPER.readTree(json));
        } catch (Exception error) {
            throw new IllegalStateException(error);
        }
    }

    @Test
    void shouldMapSuggestionsToCategoryIds() {
        when(categoryRepository.findAll()).thenReturn(List.of(food, transport));
        geminiAnswers("""
                [{"index":0,"categoryName":"Comida","confidence":0.98},
                 {"index":1,"categoryName":"Transporte","confidence":0.95}]
                """);

        var payload = new ClassifyTransactionsDTO(List.of(
                item(0, "IFOOD *RESTAURANTE", TransactionType.EXPENSE),
                item(1, "UBER *TRIP", TransactionType.EXPENSE)
        ), null);

        var result = service.classify(user, payload);

        assertEquals(2, result.classified());
        assertEquals(0, result.unmatched());
        assertEquals("cat-food", result.items().get(0).categoryId());
        assertEquals("cat-transport", result.items().get(1).categoryId());
    }

    @Test
    void shouldLeaveItemUncategorizedBelowConfidenceThreshold() {
        when(categoryRepository.findAll()).thenReturn(List.of(food));
        geminiAnswers("""
                [{"index":0,"categoryName":"Comida","confidence":0.42}]
                """);

        var payload = new ClassifyTransactionsDTO(
                List.of(item(0, "XPTO COMERCIO 8891", TransactionType.EXPENSE)), null);

        var result = service.classify(user, payload);

        assertEquals(0, result.classified());
        assertEquals(1, result.unmatched());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void shouldIgnoreCategoryOutsideTheAllowedList() {
        when(categoryRepository.findAll()).thenReturn(List.of(food));
        geminiAnswers("""
                [{"index":0,"categoryName":"Pet","confidence":0.99}]
                """);

        var payload = new ClassifyTransactionsDTO(
                List.of(item(0, "PETZ LOJA", TransactionType.EXPENSE)), null);

        var result = service.classify(user, payload);

        assertEquals(0, result.classified());
        assertEquals(1, result.unmatched());
    }

    @Test
    void shouldSendOneRequestPerTypeAndNeverMixCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(food, salary));
        geminiAnswers("""
                [{"index":0,"categoryName":"Comida","confidence":0.9}]
                """);

        var payload = new ClassifyTransactionsDTO(List.of(
                item(0, "IFOOD", TransactionType.EXPENSE),
                item(1, "PAGAMENTO CLIENTE", TransactionType.INCOME)
        ), null);

        service.classify(user, payload);

        verify(geminiClient, times(2)).generateJson(anyString(), anyString(), any());
    }

    @Test
    void shouldAskOnlyOnceForRepeatedDescriptions() {
        when(categoryRepository.findAll()).thenReturn(List.of(food));
        geminiAnswers("""
                [{"index":0,"categoryName":"Comida","confidence":0.97}]
                """);

        var payload = new ClassifyTransactionsDTO(List.of(
                item(0, "IFOOD *RESTAURANTE", TransactionType.EXPENSE),
                item(1, "ifood  *restaurante ", TransactionType.EXPENSE),
                item(2, "IFOOD *RESTAURANTE", TransactionType.EXPENSE)
        ), null);

        var result = service.classify(user, payload);

        verify(geminiClient, times(1)).generateJson(anyString(), anyString(), any());
        assertEquals(3, result.classified());
        assertTrue(result.items().stream().allMatch(entry -> entry.categoryId().equals("cat-food")));
    }

    @Test
    void shouldRestrictToSelectedCategories() {
        when(categoryRepository.findAllById(anyList())).thenReturn(List.of(transport));
        geminiAnswers("""
                [{"index":0,"categoryName":"Transporte","confidence":0.9}]
                """);

        var payload = new ClassifyTransactionsDTO(
                List.of(item(0, "UBER", TransactionType.EXPENSE)), List.of("cat-transport"));

        var result = service.classify(user, payload);

        assertEquals(1, result.classified());
        verify(categoryRepository, never()).findAll();
    }

    @Test
    void shouldThrowWhenSelectedCategoryDoesNotExist() {
        when(categoryRepository.findAllById(anyList())).thenReturn(List.of());

        var payload = new ClassifyTransactionsDTO(
                List.of(item(0, "UBER", TransactionType.EXPENSE)), List.of("missing"));

        assertThrows(CategoryNotFound.class, () -> service.classify(user, payload));

        verify(geminiClient, never()).generateJson(anyString(), anyString(), any());
    }

    @Test
    void shouldNotCallGeminiWhenNoCategoryMatchesTheType() {
        when(categoryRepository.findAll()).thenReturn(List.of(salary));

        var payload = new ClassifyTransactionsDTO(
                List.of(item(0, "IFOOD", TransactionType.EXPENSE)), null);

        var result = service.classify(user, payload);

        assertEquals(0, result.classified());
        verify(geminiClient, never()).generateJson(anyString(), anyString(), any());
    }

    @Test
    void shouldSkipItemsWithoutDescription() {
        when(categoryRepository.findAll()).thenReturn(List.of(food));

        var payload = new ClassifyTransactionsDTO(
                List.of(item(0, "   ", TransactionType.EXPENSE)), null);

        var result = service.classify(user, payload);

        assertEquals(0, result.classified());
        assertEquals(1, result.unmatched());
        verify(geminiClient, never()).generateJson(anyString(), anyString(), any());
    }

    @Test
    void shouldPropagateProviderFailure() {
        when(categoryRepository.findAll()).thenReturn(List.of(food));
        when(geminiClient.generateJson(anyString(), anyString(), any()))
                .thenThrow(new ClassificationFailedError());

        var payload = new ClassifyTransactionsDTO(
                List.of(item(0, "IFOOD", TransactionType.EXPENSE)), null);

        assertThrows(ClassificationFailedError.class, () -> service.classify(user, payload));
    }

    @Test
    void shouldNotCallGeminiWhenRateLimited() {
        var rateLimited = new ClassificationRateLimitedError(30);
        org.mockito.Mockito.doThrow(rateLimited).when(rateLimiter).checkAndRegister("user-id");

        var payload = new ClassifyTransactionsDTO(
                List.of(item(0, "IFOOD", TransactionType.EXPENSE)), null);

        var thrown = assertThrows(ClassificationRateLimitedError.class,
                () -> service.classify(user, payload));

        assertEquals(30, thrown.getRetryAfterSeconds());
        verify(geminiClient, never()).generateJson(anyString(), anyString(), any());
        verify(categoryRepository, never()).findAll();
    }

    @Test
    void shouldCheckRateLimitPerUser() {
        when(categoryRepository.findAll()).thenReturn(List.of(food));
        geminiAnswers("""
                [{"index":0,"categoryName":"Comida","confidence":0.9}]
                """);

        service.classify(user, new ClassifyTransactionsDTO(
                List.of(item(0, "IFOOD", TransactionType.EXPENSE)), null));

        verify(rateLimiter, times(1)).checkAndRegister("user-id");
    }

    @Test
    void shouldMatchCategoryNameIgnoringAccentAndCase() {
        when(categoryRepository.findAll()).thenReturn(List.of(salary));
        geminiAnswers("""
                [{"index":0,"categoryName":"salario","confidence":0.99}]
                """);

        var payload = new ClassifyTransactionsDTO(
                List.of(item(0, "PAGAMENTO SALARIO", TransactionType.INCOME)), null);

        var result = service.classify(user, payload);

        assertEquals(1, result.classified());
        assertNotNull(result.items().get(0).categoryId());
        assertEquals("cat-salary", result.items().get(0).categoryId());
    }
}
