package br.com.locus.config;

import br.com.locus.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Mantém os endpoints administrativos compatíveis com o modo local.
 * Em produção, a decisão passa a depender das roles do token.
 */
@Component("securityMode")
public class SecurityMode {

    private final boolean enabled;
    private final UsuarioRepository usuarioRepository;

    public SecurityMode(
            @Value("${app.security.enabled:false}") boolean enabled,
            UsuarioRepository usuarioRepository) {
        this.enabled = enabled;
        this.usuarioRepository = usuarioRepository;
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

    public boolean permitOrSameUser(Authentication authentication, Long usuarioId) {
        if (!enabled) {
            return true;
        }
        if (authentication == null || !authentication.isAuthenticated() || usuarioId == null) {
            return false;
        }
        if (permitOrHasAdmin(authentication)) {
            return true;
        }

        String uid = authentication.getName();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            uid = jwt.getClaimAsString("user_id");
            if (uid == null || uid.isBlank()) {
                uid = jwt.getSubject();
            }
            if (uid != null && usuarioRepository.findByFirebaseUid(uid)
                    .map(usuario -> usuario.getId().equals(usuarioId))
                    .orElse(false)) {
                return true;
            }
            String email = jwt.getClaimAsString("email");
            return email != null && usuarioRepository.findByEmailIgnoreCase(email)
                    .map(usuario -> usuario.getId().equals(usuarioId))
                    .orElse(false);
        }
        return uid != null && usuarioRepository.findByFirebaseUid(uid)
                .map(usuario -> usuario.getId().equals(usuarioId))
                .orElse(false);
    }
}
