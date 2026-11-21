package br.com.locus.integracao.gemini;

import br.com.locus.integracao.ExternalApiClientSupport;
import br.com.locus.integracao.IntegrationProperties;
import tools.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
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
        String model = modelAtual();

        try {
            return enviar(body, model);
        } catch (RestClientResponseException exception) {
            // O grounding do Google Search depende do modelo, da conta e da
            // quota. Se ele for recusado, a conversa ainda deve responder:
            // Places/Routes continuam fazendo a parte geográfica do Locus.
            if (properties.isWebSearchEnabled()
                    && (exception.getStatusCode().value() == 400
                    || exception.getStatusCode().value() == 404
                    || exception.getStatusCode().value() == 422)) {
                body.remove("tools");
                try {
                    return enviar(body, model);
                } catch (RestClientResponseException segundaFalha) {
                    throw ExternalApiClientSupport.translate(PROVIDER, segundaFalha);
                } catch (RestClientException segundaFalha) {
                    throw ExternalApiClientSupport.translate(PROVIDER, segundaFalha);
                }
            }
            throw ExternalApiClientSupport.translate(PROVIDER, exception);
        } catch (RestClientException exception) {
            throw ExternalApiClientSupport.translate(PROVIDER, exception);
        }
    }

    private JsonNode enviar(Map<String, Object> body, String model) {
        return client()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", properties.getApiKey())
                        .build(model))
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
    }

    private RestClient client() {
        return ExternalApiClientSupport.client(
                properties.getBaseUrl(),
                properties.getTimeoutMs()
        );
    }

    private String modelAtual() {
        String model = properties.getModel();
        if (model == null || model.isBlank()) {
            return "gemini-3.5-flash";
        }
        String normalizado = model.trim();
        if ((normalizado.startsWith("\"") && normalizado.endsWith("\""))
                || (normalizado.startsWith("'") && normalizado.endsWith("'"))) {
            normalizado = normalizado.substring(1, normalizado.length() - 1).trim();
        }
        // gemini-2.0-flash foi descontinuado. Mantém a aplicação funcionando
        // mesmo se a variável antiga ainda estiver salva no Railway.
        return normalizado.equalsIgnoreCase("gemini-2.0-flash")
                ? "gemini-3.5-flash"
                : normalizado;
    }
}
