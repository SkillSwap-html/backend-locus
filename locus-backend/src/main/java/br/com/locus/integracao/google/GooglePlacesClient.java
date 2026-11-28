package br.com.locus.integracao.google;

import br.com.locus.integracao.ExternalApiClientSupport;
import br.com.locus.integracao.IntegrationProperties;
import tools.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GooglePlacesClient {

    private static final String PROVIDER = "Google Places API";

    private final IntegrationProperties.Places properties;

    public GooglePlacesClient(IntegrationProperties properties) {
        this.properties = properties.getGoogle().getPlaces();
    }

    public JsonNode textSearch(String textQuery, String languageCode) {
        requireConfiguration();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("textQuery", textQuery);
        if (languageCode != null && !languageCode.isBlank()) {
            body.put("languageCode", languageCode);
        }

        return post("/v1/places:searchText", body,
                "places.displayName,places.formattedAddress,places.location,"
                        + "places.id,places.rating,places.priceLevel,places.types,"
                        + "places.currentOpeningHours,places.editorialSummary,places.photos");
    }

    public byte[] photo(String photoName, Integer maxWidthPx) {
        requireConfiguration();
        if (photoName == null || photoName.isBlank() || !photoName.startsWith("places/")) {
            throw new IllegalArgumentException("Nome de foto do Google Places inválido.");
        }
        try {
            return client()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/" + photoName + "/media")
                            .queryParam("maxWidthPx", maxWidthPx == null ? 900 : Math.max(320, Math.min(maxWidthPx, 1600)))
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

    public JsonNode autocomplete(String input, String sessionToken) {
        requireConfiguration();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("input", input);
        if (sessionToken != null && !sessionToken.isBlank()) {
            body.put("sessionToken", sessionToken);
        }

        return post("/v1/places:autocomplete", body,
                "suggestions.placePrediction.placeId,"
                        + "suggestions.placePrediction.text");
    }

    public JsonNode details(String placeId) {
        requireConfiguration();

        try {
            return client()
                    .get()
                    .uri("/v1/places/" + placeId)
                    .header("X-Goog-Api-Key", properties.getApiKey())
                    .header("X-Goog-FieldMask",
                            "id,displayName,formattedAddress,location,rating,"
                                    + "priceLevel,types,currentOpeningHours,"
                                    + "editorialSummary,photos")
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw ExternalApiClientSupport.translate(PROVIDER, exception);
        } catch (RestClientException exception) {
            throw ExternalApiClientSupport.translate(PROVIDER, exception);
        }
    }

    private JsonNode post(
            String path,
            Map<String, Object> body,
            String fieldMask) {

        try {
            return client()
                    .post()
                    .uri(path)
                    .header("X-Goog-Api-Key", properties.getApiKey())
                    .header("X-Goog-FieldMask", fieldMask)
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

    private void requireConfiguration() {
        ExternalApiClientSupport.requireEnabled(
                PROVIDER,
                properties.isEnabled(),
                properties.getApiKey()
        );
    }
}
