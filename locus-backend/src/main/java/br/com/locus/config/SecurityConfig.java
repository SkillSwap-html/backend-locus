package br.com.locus.config;

import br.com.locus.usuario.TipoUsuario;
import br.com.locus.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final boolean securityEnabled;
    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(
            @Value("${app.security.enabled:false}") boolean securityEnabled,
            UsuarioRepository usuarioRepository) {

        this.securityEnabled = securityEnabled;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults());

        if (!securityEnabled) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/error", "/api/teste").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.security.enabled", havingValue = "true")
    public JwtDecoder jwtDecoder(
            @Value("${app.firebase.project-id}") String projectId) {

        if (projectId == null || projectId.isBlank()) {
            throw new IllegalStateException(
                    "FIREBASE_PROJECT_ID deve ser informado quando SECURITY_ENABLED=true."
            );
        }

        String issuer = "https://securetoken.google.com/" + projectId;
        String googleJwksUrl =
                "https://www.googleapis.com/service_accounts/v1/jwk/"
                        + "securetoken@system.gserviceaccount.com";

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder.withJwkSetUri(googleJwksUrl).build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {
            List<String> audience = jwt.getAudience();
            if (audience != null && audience.contains(projectId)) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error(
                            "invalid_token",
                            "The token audience does not match the Firebase project.",
                            null
                    )
            );
        };
        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator)
        );

        return decoder;
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("user_id");
        converter.setJwtGrantedAuthoritiesConverter(this::authoritiesFromClaims);
        return converter;
    }

    private Collection<GrantedAuthority> authoritiesFromClaims(Jwt jwt) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        String role = jwt.getClaimAsString("role");

        if (role != null && !role.isBlank()) {
            authorities.add(new SimpleGrantedAuthority(
                    "ROLE_" + role.toUpperCase()
            ));
        }

        Boolean admin = jwt.getClaim("admin");
        if (Boolean.TRUE.equals(admin)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
        }

        String email = jwt.getClaimAsString("email");
        if (email != null) {
            usuarioRepository.findByEmailIgnoreCase(email).ifPresent(usuario -> {
                if (usuario.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
                }
                if (usuario.getTipoUsuario() == TipoUsuario.RESPONSAVEL_TECNICO) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_RESPONSAVEL_TECNICO"));
                }
            });
        }

        return authorities;
    }
}
