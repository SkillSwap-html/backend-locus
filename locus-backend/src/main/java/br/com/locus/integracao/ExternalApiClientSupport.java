package br.com.locus.integracao;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClientException;

public final class ExternalApiClientSupport {

    private ExternalApiClientSupport() {
    }

    public static RestClient client(String baseUrl, int timeoutMs) {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public static void requireEnabled(
            String provider,
            boolean enabled,
            String apiKey) {

        if (!enabled) {
            throw new ExternalApiException(
                    provider,
                    503,
                    "A integração com " + provider
                            + " está desabilitada. Ative-a nas variáveis de ambiente."
            );
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new ExternalApiException(
                    provider,
                    503,
                    "A chave da integração com " + provider
                            + " não foi configurada."
            );
        }
    }

    public static ExternalApiException translate(
            String provider,
            RestClientResponseException exception) {

        HttpStatusCode statusCode = exception.getStatusCode();
        return new ExternalApiException(
                provider,
                statusCode.value(),
                "A integração com " + provider
                        + " retornou um erro HTTP " + statusCode.value() + "."
        );
    }

    public static ExternalApiException translate(
            String provider,
            RestClientException exception) {

        return new ExternalApiException(
                provider,
                504,
                "A integração com " + provider
                        + " não respondeu dentro do tempo configurado."
        );
    }
}
