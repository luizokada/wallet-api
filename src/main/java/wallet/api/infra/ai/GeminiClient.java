package wallet.api.infra.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import wallet.api.errors.ai.ClassificationFailedError;
import wallet.api.errors.ai.ClassificationUnavailableError;

import java.util.List;
import java.util.Map;

@Service
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private final RestClient geminiRestClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    public GeminiClient(RestClient geminiRestClient, ObjectMapper objectMapper) {
        this.geminiRestClient = geminiRestClient;
        this.objectMapper = objectMapper;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public JsonNode generateJson(String systemInstruction, String prompt, Map<String, Object> responseSchema) {
        if (!isConfigured()) {
            throw new ClassificationUnavailableError();
        }

        var requestBody = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", systemInstruction))),
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", 0,
                        "responseMimeType", "application/json",
                        "responseSchema", responseSchema
                )
        );

        try {
            var response = geminiRestClient.post()
                    .uri("/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            var generatedText = extractText(response);
            if (generatedText == null) {
                log.error("Gemini returned no usable content: {}", response);
                throw new ClassificationFailedError();
            }

            return objectMapper.readTree(generatedText);
        } catch (ClassificationFailedError error) {
            throw error;
        } catch (Exception error) {
            log.error("Gemini request failed: {}", error.getMessage());
            throw new ClassificationFailedError();
        }
    }

    private String extractText(JsonNode response) {
        if (response == null) {
            return null;
        }
        var text = response.path("candidates").path(0).path("content").path("parts").path(0).path("text");
        return text.isTextual() ? text.asText() : null;
    }
}
