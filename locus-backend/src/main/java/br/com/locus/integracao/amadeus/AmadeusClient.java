package br.com.locus.integracao.amadeus;

import br.com.locus.integracao.ExternalApiClientSupport;
import br.com.locus.integracao.ExternalApiException;
import br.com.locus.integracao.IntegrationProperties;
//import com.fasterxml.jackson.databind.JsonNode;
import tools.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AmadeusClient {

    private static final String PROVIDER = "Amadeus API";

    private final IntegrationProperties.Amadeus properties;
    private final AmadeusMockData mockData;
    private final Object tokenLock = new Object();
    private volatile AccessToken cachedToken;

    public AmadeusClient(
            IntegrationProperties properties,
            AmadeusMockData mockData) {
        this.properties = properties.getAmadeus();
        this.mockData = mockData;
    }

    public JsonNode searchFlightOffers(Map<String, String> filters) {
        if (isMockMode()) {
            return mockData.searchFlights(filters);
        }
        return getWithToken(
                "/v2/shopping/flight-offers",
                filters
        );
    }

    public JsonNode priceFlightOffer(JsonNode flightOffer) {
        if (isMockMode()) {
            return mockData.priceFlight(flightOffer);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", Map.of(
                "type", "flight-offers-pricing",
                "flightOffers", java.util.List.of(flightOffer)
        ));
        return postWithToken("/v1/shopping/flight-offers/pricing", body);
    }

    public JsonNode listHotels(String cityCode) {
        if (isMockMode()) {
            return mockData.listHotels(cityCode);
        }
        return getWithToken(
                "/v1/reference-data/locations/hotels/by-city",
                Map.of("cityCode", cityCode)
        );
    }

    public JsonNode searchHotels(Map<String, String> filters) {
        if (isMockMode()) {
            return mockData.searchHotels(filters);
        }
        return getWithToken(
                "/v3/shopping/hotel-offers",
                filters
        );
    }

    private JsonNode getWithToken(
            String path,
            Map<String, String> filters) {

        requireConfiguration();

        try {
            return client()
                    .get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(path);
                        filters.forEach((name, value) -> {
                            if (value != null && !value.isBlank()) {
                                uriBuilder.queryParam(name, value);
                            }
                        });
                        return uriBuilder.build();
                    })
                    .header("Authorization", "Bearer " + accessToken())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw ExternalApiClientSupport.translate(PROVIDER, exception);
        } catch (RestClientException exception) {
            throw ExternalApiClientSupport.translate(PROVIDER, exception);
        }
    }

    private JsonNode postWithToken(
            String path,
            Map<String, Object> body) {

        requireConfiguration();

        try {
            return client()
                    .post()
                    .uri(path)
                    .header("Authorization", "Bearer " + accessToken())
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

    private String accessToken() {
        if (properties.getAccessToken() != null
                && !properties.getAccessToken().isBlank()) {
            return properties.getAccessToken();
        }

        AccessToken current = cachedToken;
        if (current != null && current.expiresAt().isAfter(Instant.now())) {
            return current.value();
        }

        synchronized (tokenLock) {
            current = cachedToken;
            if (current != null
                    && current.expiresAt().isAfter(Instant.now())) {
                return current.value();
            }

            try {
                MultiValueMap<String, String> form =
                        new LinkedMultiValueMap<>();
                form.add("grant_type", "client_credentials");
                form.add("client_id", properties.getClientId());
                form.add("client_secret", properties.getClientSecret());

                JsonNode response = client()
                        .post()
                        .uri("/v1/security/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(form)
                        .retrieve()
                        .body(JsonNode.class);

                String value = response.path("access_token").asText();
                long expiresIn = response.path("expires_in").asLong(1800);
                if (value.isBlank()) {
                    throw new ExternalApiException(
                            PROVIDER,
                            502,
                            "A resposta de autenticação da Amadeus não contém access_token."
                    );
                }

                cachedToken = new AccessToken(
                        value,
                        Instant.now().plusSeconds(Math.max(30, expiresIn - 30))
                );
                return value;
            } catch (RestClientResponseException exception) {
                throw ExternalApiClientSupport.translate(PROVIDER, exception);
            } catch (RestClientException exception) {
                throw ExternalApiClientSupport.translate(PROVIDER, exception);
            }
        }
    }

    private RestClient client() {
        return ExternalApiClientSupport.client(
                properties.getBaseUrl(),
                properties.getTimeoutMs()
        );
    }

    private void requireConfiguration() {
        if ("disabled".equalsIgnoreCase(properties.getMode())
                || !properties.isEnabled()) {
            throw new ExternalApiException(
                    PROVIDER,
                    503,
                    "A integração de ofertas de viagem está desabilitada. "
                            + "Use AMADEUS_MODE=mock para o catálogo simulado "
                            + "ou AMADEUS_MODE=real com as credenciais do provedor."
            );
        }

        if ((properties.getAccessToken() == null
                || properties.getAccessToken().isBlank())
                && (properties.getClientId() == null
                || properties.getClientId().isBlank()
                || properties.getClientSecret() == null
                || properties.getClientSecret().isBlank())) {
            throw new ExternalApiException(
                    PROVIDER,
                    503,
                    "Configure AMADEUS_ACCESS_TOKEN ou AMADEUS_CLIENT_ID "
                            + "e AMADEUS_CLIENT_SECRET."
            );
        }
    }

    private boolean isMockMode() {
        return !"real".equalsIgnoreCase(properties.getMode())
                && !"disabled".equalsIgnoreCase(properties.getMode());
    }

    private record AccessToken(String value, Instant expiresAt) {
    }
}
