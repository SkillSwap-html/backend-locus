package br.com.locus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Mantém os endpoints administrativos compatíveis com o modo local.
 * Em produção, a decisão passa a depender das roles do token.
 */
@Component("securityMode")
public class SecurityMode {

    private final boolean enabled;

    public SecurityMode(
            @Value("${app.security.enabled:false}") boolean enabled) {
        this.enabled = enabled;
    }

    public boolean permitOrHasAdmin(Authentication authentication) {
        if (!enabled) {
            return true;
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        "ROLE_ADMINISTRADOR".equals(authority.getAuthority())
                                || "ROLE_RESPONSAVEL_TECNICO".equals(authority.getAuthority()));
    }
}
