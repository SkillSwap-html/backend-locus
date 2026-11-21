package br.com.locus.admin;

import br.com.locus.exception.AcessoNegadoException;
import br.com.locus.usuario.TipoUsuario;
import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;


@Service
public class ControleAcessoService {

    private final UsuarioRepository usuarioRepository;
    private final ViagemRepository viagemRepository;

    public ControleAcessoService(
            UsuarioRepository usuarioRepository,
            ViagemRepository viagemRepository) {

        this.usuarioRepository = usuarioRepository;
        this.viagemRepository = viagemRepository;
    }

    public Usuario buscarUsuario(Long usuarioId) {

        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuário não encontrado."
                        )
                );
    }

    public Usuario exigirAdministrador(Long usuarioId) {

        Usuario usuario = exigirSolicitanteAutenticado(usuarioId);

        if (usuario.getTipoUsuario()
                != TipoUsuario.ADMINISTRADOR) {

            throw new AcessoNegadoException(
                    "Apenas administradores podem realizar esta operação."
            );
        }

        return usuario;
    }

    public Usuario exigirCuradorOuAdministrador(Long usuarioId) {

        Usuario usuario = exigirSolicitanteAutenticado(usuarioId);

        if (usuario.getTipoUsuario() != TipoUsuario.CURADOR_CONTEUDO
                && usuario.getTipoUsuario() != TipoUsuario.ADMINISTRADOR) {

            throw new AcessoNegadoException(
                    "Apenas administradores ou curadores de conteúdo podem realizar esta operação."
            );
        }

        return usuario;
    }

    public Usuario exigirResponsavelTecnicoOuAdministrador(
            Long usuarioId) {

        Usuario usuario = exigirSolicitanteAutenticado(usuarioId);

        if (usuario.getTipoUsuario()
                != TipoUsuario.RESPONSAVEL_TECNICO
                && usuario.getTipoUsuario()
                != TipoUsuario.ADMINISTRADOR) {

            throw new AcessoNegadoException(
                    "Apenas administradores ou responsáveis técnicos podem realizar esta operação."
            );
        }

        return usuario;
    }

    public Viagem exigirProprietarioViagem(
            Long viagemId,
            Long usuarioId) {

        Usuario usuario = exigirSolicitanteAutenticado(usuarioId);

        Viagem viagem = viagemRepository.findById(viagemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Viagem não encontrada."
                        )
                );

        if (!viagem.getUsuario().getId()
                .equals(usuario.getId())) {

            throw new AcessoNegadoException(
                    "Apenas o proprietário da viagem pode realizar esta operação."
            );
        }

        return viagem;
    }

    public Usuario exigirMesmoUsuario(
            Long usuarioId,
            Long usuarioSolicitanteId) {

        Usuario usuario = buscarUsuario(usuarioId);
        Usuario usuarioSolicitante = exigirSolicitanteAutenticado(usuarioSolicitanteId);

        if (!usuario.getId().equals(usuarioSolicitante.getId())) {
            throw new AcessoNegadoException(
                    "O usuário não possui permissão para acessar estes dados."
            );
        }

        return usuario;
    }

    /**
     * O ID enviado pelo navegador serve apenas para manter compatibilidade
     * com clientes antigos. Com Firebase ativo, a identidade confiável é o
     * UID/e-mail do JWT validado pelo Spring Security.
     */
    public Usuario exigirSolicitanteAutenticado(Long usuarioSolicitanteId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return buscarUsuario(usuarioSolicitanteId);
        }

        Usuario autenticado = usuarioDoToken(authentication);
        if (autenticado == null) {
            throw new AcessoNegadoException(
                    "Não foi possível identificar o usuário autenticado. Entre novamente."
            );
        }

        if (usuarioSolicitanteId == null
                || !autenticado.getId().equals(usuarioSolicitanteId)) {
            throw new AcessoNegadoException(
                    "O usuário autenticado não corresponde ao usuário solicitado."
            );
        }

        return autenticado;
    }

    private Usuario usuarioDoToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            String uid = primeiroTexto(
                    jwt.getClaimAsString("user_id"),
                    jwt.getSubject()
            );
            if (uid != null) {
                Usuario porUid = usuarioRepository.findByFirebaseUid(uid).orElse(null);
                if (porUid != null) return porUid;
            }
            String email = jwt.getClaimAsString("email");
            if (email != null && !email.isBlank()) {
                return usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
            }
        }
        return null;
    }

    private String primeiroTexto(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) return valor;
        }
        return null;
    }
}
