package br.com.locus.integracao.gemini;

import br.com.locus.integracao.ExternalApiClientSupport;
import br.com.locus.integracao.IntegrationProperties;
import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GeminiClient {

    private static final String PROVIDER = "Gemini API";

    private final IntegrationProperties.Gemini properties;

    public GeminiClient(IntegrationProperties properties) {
        this.properties = properties.getGemini();
    }

    public JsonNode generateContent(String prompt) {
        ExternalApiClientSupport.requireEnabled(
                PROVIDER,
                properties.isEnabled(),
                properties.getApiKey()
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(Map.of("text", prompt))
                        )
                ));
        body.put("generationConfig", Map.of(
                        "temperature", properties.getTemperature(),
                        "maxOutputTokens", properties.getMaxOutputTokens()
                ));
        if (properties.isWebSearchEnabled()) {
            body.put("tools", List.of(Map.of("google_search", Map.of())));
        }

        try {
            return client()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/{model}:generateContent")
                            .queryParam("key", properties.getApiKey())
                            .build(properties.getModel()))
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw ExternalApiClientSupport.translate(PROVIDER, exception);
        } catch (RestClientException exception) {
            throw ExternalApiClientSupport.translate(PROVIDER, exception);
        }
    }

    private RestClient client() {
        return ExternalApiClientSupport.client(
                properties.getBaseUrl(),
                properties.getTimeoutMs()
        );
    }
}
