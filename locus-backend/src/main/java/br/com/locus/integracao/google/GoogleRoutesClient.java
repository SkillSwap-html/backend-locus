package br.com.locus.integracao.google;

import br.com.locus.integracao.ExternalApiClientSupport;
import br.com.locus.integracao.IntegrationProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Component
public class GoogleRoutesClient {

    private static final String PROVIDER = "Google Routes API";

    private final IntegrationProperties.Routes properties;

    public GoogleRoutesClient(IntegrationProperties properties) {
        this.properties = properties.getGoogle().getRoutes();
    }

    public JsonNode computeRoute(Map<String, Object> request) {
        ExternalApiClientSupport.requireEnabled(
                PROVIDER,
                properties.isEnabled(),
                properties.getApiKey()
        );

        try {
            return client()
                    .post()
                    .uri("/directions/v2:computeRoutes")
                    .header("X-Goog-Api-Key", properties.getApiKey())
                    .header("X-Goog-FieldMask",
                            "routes.duration,routes.distanceMeters,"
                                    + "routes.polyline.encodedPolyline,routes.legs")
                    .body(request)
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
