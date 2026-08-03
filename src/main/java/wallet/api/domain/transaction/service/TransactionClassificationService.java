package wallet.api.domain.transaction.service;

import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.category.repository.CategoryRepository;
import wallet.api.domain.transaction.dto.ClassificationResultDTO;
import wallet.api.domain.transaction.dto.ClassifyTransactionsDTO;
import wallet.api.domain.transaction.dto.ClassifyTransactionsDTO.ClassifyItemDTO;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.domain.user.entity.User;
import wallet.api.errors.category.CategoryNotFound;
import wallet.api.infra.ai.ClassificationRateLimiter;
import wallet.api.infra.ai.GeminiClient;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionClassificationService {

    private static final String SYSTEM_INSTRUCTION = """
            Você classifica lançamentos de fatura de cartão e extratos bancários brasileiros.
            Para cada lançamento, escolha exatamente UMA categoria da lista permitida.
            Use o nome da categoria exatamente como aparece na lista.
            Se nenhuma categoria servir, devolva categoryName vazio.
            Nunca invente uma categoria que não esteja na lista.
            confidence é sua certeza de 0 a 1.
            Responda para todos os índices recebidos.
            """;

    private static final Map<String, Object> RESPONSE_SCHEMA = Map.of(
            "type", "ARRAY",
            "items", Map.of(
                    "type", "OBJECT",
                    "properties", Map.of(
                            "index", Map.of("type", "INTEGER"),
                            "categoryName", Map.of("type", "STRING"),
                            "confidence", Map.of("type", "NUMBER")),
                    "required", List.of("index", "categoryName", "confidence")));

    private final CategoryRepository categoryRepository;
    private final GeminiClient geminiClient;
    private final ClassificationRateLimiter rateLimiter;

    @Value("${gemini.min-confidence}")
    private double minConfidence;

    public TransactionClassificationService(
            CategoryRepository categoryRepository,
            GeminiClient geminiClient,
            ClassificationRateLimiter rateLimiter) {
        this.categoryRepository = categoryRepository;
        this.geminiClient = geminiClient;
        this.rateLimiter = rateLimiter;
    }

    public ClassificationResultDTO classify(User currentUser, ClassifyTransactionsDTO payload) {
        rateLimiter.checkAndRegister(currentUser.getId());

        var allowedCategories = loadAllowedCategories(currentUser.getId(), payload.categoryIds());
        var classifiedItems = new ArrayList<ClassificationResultDTO.ClassifiedItemDTO>();

        for (var type : TransactionType.values()) {
            var itemsOfType = payload.items().stream()
                    .filter(item -> item.type() == type)
                    .filter(item -> item.description() != null && !item.description().isBlank())
                    .toList();

            var categoriesOfType = allowedCategories.stream()
                    .filter(category -> category.getType() == type)
                    .toList();

            if (itemsOfType.isEmpty() || categoriesOfType.isEmpty()) {
                continue;
            }

            classifiedItems.addAll(classifyGroup(itemsOfType, categoriesOfType));
        }

        return new ClassificationResultDTO(
                classifiedItems.size(),
                payload.items().size() - classifiedItems.size(),
                classifiedItems);
    }

    private List<ClassificationResultDTO.ClassifiedItemDTO> classifyGroup(
            List<ClassifyItemDTO> items,
            List<Category> categories) {
        var uniqueDescriptions = new ArrayList<String>();
        var descriptionPositions = new LinkedHashMap<String, List<Integer>>();

        for (var item : items) {
            var key = normalize(item.description());
            if (!descriptionPositions.containsKey(key)) {
                descriptionPositions.put(key, new ArrayList<>());
                uniqueDescriptions.add(item.description().trim());
            }
            descriptionPositions.get(key).add(item.index());
        }

        var suggestionsByPosition = askGemini(uniqueDescriptions, categories);
        var categoriesByName = categories.stream()
                .collect(Collectors.toMap(category -> normalize(category.getName()), category -> category,
                        (first, second) -> first));

        var results = new ArrayList<ClassificationResultDTO.ClassifiedItemDTO>();
        var positions = new ArrayList<>(descriptionPositions.values());

        for (var entry : suggestionsByPosition.entrySet()) {
            var position = entry.getKey();
            var suggestion = entry.getValue();

            if (position < 0 || position >= positions.size()) {
                continue;
            }
            if (suggestion.confidence() < minConfidence) {
                continue;
            }

            var category = categoriesByName.get(normalize(suggestion.categoryName()));
            if (category == null) {
                continue;
            }

            for (var index : positions.get(position)) {
                results.add(new ClassificationResultDTO.ClassifiedItemDTO(
                        index, category.getId(), category.getName(), suggestion.confidence()));
            }
        }

        return results;
    }

    private Map<Integer, Suggestion> askGemini(List<String> descriptions, List<Category> categories) {
        var categoryNames = categories.stream().map(Category::getName).collect(Collectors.joining(", "));
        var numberedDescriptions = new StringBuilder();
        for (var position = 0; position < descriptions.size(); position += 1) {
            numberedDescriptions.append(position).append(". ").append(descriptions.get(position)).append('\n');
        }

        var prompt = "Categorias permitidas: %s%n%nLançamentos:%n%s".formatted(categoryNames, numberedDescriptions);
        var response = geminiClient.generateJson(SYSTEM_INSTRUCTION, prompt, RESPONSE_SCHEMA);

        var suggestions = new HashMap<Integer, Suggestion>();
        if (response == null || !response.isArray()) {
            return suggestions;
        }

        for (JsonNode node : response) {
            var categoryName = node.path("categoryName").asString("");
            if (categoryName.isBlank()) {
                continue;
            }
            suggestions.put(node.path("index").asInt(-1),
                    new Suggestion(categoryName, node.path("confidence").asDouble(0)));
        }

        return suggestions;
    }

    private List<Category> loadAllowedCategories(String userId, List<String> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return categoryRepository.findAllVisible(userId);
        }

        var found = categoryRepository.findAllVisibleByIdIn(userId, categoryIds);
        if (found.size() != categoryIds.stream().distinct().count()) {
            throw new CategoryNotFound();
        }

        return found;
    }

    private String normalize(String value) {
        return Normalizer.normalize(value.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("\\s+", " ");
    }

    private record Suggestion(String categoryName, double confidence) {
    }
}
