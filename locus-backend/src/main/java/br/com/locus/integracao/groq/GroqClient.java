package br.com.locus.integracao.groq;

import br.com.locus.integracao.ExternalApiClientSupport;
import br.com.locus.integracao.IntegrationProperties;
import tools.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Component
public class GroqClient {

    private static final String PROVIDER = "Groq API";

    private final IntegrationProperties.Groq properties;

    public GroqClient(IntegrationProperties properties) {
        this.properties = properties.getGroq();
    }

    public JsonNode generateContent(String prompt) {
        ExternalApiClientSupport.requireEnabled(
                PROVIDER,
                properties.isEnabled(),
                properties.getApiKey()
        );

        Map<String, Object> body = Map.of(
                "model", properties.getModel(),
                "messages", List.of(Map.of(
                        "role", "user",
                        "content", prompt
                )),
                "temperature", properties.getTemperature(),
                "max_tokens", properties.getMaxOutputTokens()
        );

        try {
            return client()
                    .post()
                    .uri("/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
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
