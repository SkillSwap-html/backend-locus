package br.com.locus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:5173,https://locus-frontend-production.up.railway.app}")
            String allowedOrigins) {

        CorsConfiguration configuration = new CorsConfiguration();
        String origensConfiguradas = normalizarTextoConfiguracao(allowedOrigins);
        List<String> origens = new ArrayList<>(
                Arrays.stream(origensConfiguradas.split(","))
                        .map(String::trim)
                        .map(this::normalizarOrigem)
                        .filter(origin -> !origin.isBlank())
                        .distinct()
                        .toList()
        );
        // Mantém o endereço publicado autorizado mesmo se o valor do Railway
        // vier vazio, com aspas ou tiver sido copiado com barras de escape.
        String frontendPublicado = "https://locus-frontend-production.up.railway.app";
        if (!origens.contains(frontendPublicado)) {
            origens.add(frontendPublicado);
        }
        configuration.setAllowedOrigins(origens);
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Cache-Control",
                "Pragma"
        ));
        configuration.setExposedHeaders(List.of("Content-Disposition"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private String normalizarOrigem(String origem) {
        String normalizada = normalizarTextoConfiguracao(origem);
        return normalizada.replaceFirst("/+$", "");
    }

    private String normalizarTextoConfiguracao(String valor) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.startsWith("\"") || normalizado.startsWith("'")) {
            normalizado = normalizado.substring(1).trim();
        }
        if (normalizado.endsWith("\"") || normalizado.endsWith("'")) {
            normalizado = normalizado.substring(0, normalizado.length() - 1).trim();
        }
        // Railway/arquivos exportados às vezes chegam com a URL escapada
        // (https\://...). O navegador envia o Origin sem essas barras.
        return normalizado
                .replace("\\:", ":")
                .replace("\\/", "/")
                .replace("\\,", ",");
    }
}
