package br.com.locus.integracao.google;

import br.com.locus.integracao.ExternalApiClientSupport;
import br.com.locus.integracao.IntegrationProperties;
import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class GoogleStreetViewClient {

    private static final String PROVIDER = "Google Street View API";

    private final IntegrationProperties.StreetView properties;

    public GoogleStreetViewClient(IntegrationProperties properties) {
        this.properties = properties.getGoogle().getStreetView();
    }

    public JsonNode metadata(String location, Integer radius) {
        ExternalApiClientSupport.requireEnabled(
                PROVIDER,
                properties.isEnabled(),
                properties.getApiKey()
        );

        try {
            return client()
                    .get()
                    .uri(uriBuilder -> {
                        uriBuilder
                                .path("/maps/api/streetview/metadata")
                                .queryParam("location", location)
                                .queryParam("key", properties.getApiKey());
                        if (radius != null) {
                            uriBuilder.queryParam("radius", radius);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw ExternalApiClientSupport.translate(PROVIDER, exception);
        } catch (RestClientException exception) {
            throw ExternalApiClientSupport.translate(PROVIDER, exception);
        }
    }

    public byte[] image(String location, Integer width, Integer height) {
        ExternalApiClientSupport.requireEnabled(
                PROVIDER,
                properties.isEnabled(),
                properties.getApiKey()
        );

        try {
            return client()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/maps/api/streetview")
                            .queryParam("size", (width == null ? 640 : width) + "x" + (height == null ? 360 : height))
                            .queryParam("location", location)
                            .queryParam("key", properties.getApiKey())
                            .build())
                    .retrieve()
                    .body(byte[].class);
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
