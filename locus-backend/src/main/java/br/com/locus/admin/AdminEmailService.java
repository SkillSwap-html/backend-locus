package br.com.locus.admin;

import br.com.locus.admin.dto.AdminEmailResposta;
import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.auditoria.ResultadoAuditoria;
import br.com.locus.usuario.TipoUsuario;
import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class AdminEmailService {

    private final AdminEmailRepository adminEmailRepository;
    private final UsuarioRepository usuarioRepository;
    private final ControleAcessoService controleAcessoService;
    private final EventoAuditoriaService eventoAuditoriaService;

    public AdminEmailService(
            AdminEmailRepository adminEmailRepository,
            UsuarioRepository usuarioRepository,
            ControleAcessoService controleAcessoService,
            EventoAuditoriaService eventoAuditoriaService) {
        this.adminEmailRepository = adminEmailRepository;
        this.usuarioRepository = usuarioRepository;
        this.controleAcessoService = controleAcessoService;
        this.eventoAuditoriaService = eventoAuditoriaService;
    }

    @Transactional
    public AdminEmailResposta adicionar(String email, Long usuarioSolicitanteId) {
        controleAcessoService.exigirAdministrador(usuarioSolicitanteId);
        String normalizado = normalizar(email);

        adminEmailRepository.findByEmailIgnoreCase(normalizado)
                .orElseGet(() -> adminEmailRepository.save(new AdminEmail(normalizado)));

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(normalizado).orElse(null);
        if (usuario != null && usuario.getTipoUsuario() != TipoUsuario.ADMINISTRADOR) {
            usuario.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            usuarioRepository.save(usuario);
        }

        eventoAuditoriaService.registrar(
                usuarioSolicitanteId,
                "ADICIONAR_ADMINISTRADOR",
                "ADMIN_EMAIL",
                usuario == null ? null : usuario.getId(),
                ResultadoAuditoria.SUCESSO,
                "E-mail autorizado para administração: " + normalizado
        );

        return resposta(normalizado, usuario);
    }

    @Transactional(readOnly = true)
    public List<AdminEmailResposta> listar() {
        return adminEmailRepository.findAllByOrderByEmailAsc()
                .stream()
                .map(item -> resposta(
                        item.getEmail(),
                        usuarioRepository.findByEmailIgnoreCase(item.getEmail()).orElse(null)))
                .toList();
    }

    @Transactional
    public void remover(String email, Long usuarioSolicitanteId) {
        controleAcessoService.exigirAdministrador(usuarioSolicitanteId);
        String normalizado = normalizar(email);
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(normalizado).orElse(null);

        if (usuario != null && usuario.getTipoUsuario() == TipoUsuario.ADMINISTRADOR
                && usuarioRepository.countByTipoUsuario(TipoUsuario.ADMINISTRADOR) <= 1) {
            throw new RuntimeException("Não é possível remover o último administrador.");
        }

        adminEmailRepository.findByEmailIgnoreCase(normalizado)
                .ifPresent(adminEmailRepository::delete);
        if (usuario != null && usuario.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
            usuario.setTipoUsuario(TipoUsuario.VIAJANTE);
            usuarioRepository.save(usuario);
        }
    }

    public boolean estaAutorizado(String email) {
        return email != null && adminEmailRepository.existsByEmailIgnoreCase(normalizar(email));
    }

    private AdminEmailResposta resposta(String email, Usuario usuario) {
        return new AdminEmailResposta(
                email,
                usuario != null,
                usuario == null ? null : usuario.getId(),
                usuario == null ? null : usuario.getNome()
        );
    }

    private String normalizar(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new RuntimeException("Informe um e-mail válido.");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
