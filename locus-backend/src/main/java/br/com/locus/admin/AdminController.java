package br.com.locus.admin;

import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.usuario.TipoUsuario;
import br.com.locus.usuario.Usuario;
import org.springframework.web.bind.annotation.*;
import br.com.locus.admin.dto.UsuarioAdminResposta;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final ControleAcessoService controleAcessoService;
    private final EventoAuditoriaService eventoAuditoriaService;

    public AdminController(
            AdminService adminService,
            ControleAcessoService controleAcessoService,
            EventoAuditoriaService eventoAuditoriaService) {

        this.adminService = adminService;
        this.controleAcessoService = controleAcessoService;
        this.eventoAuditoriaService = eventoAuditoriaService;
    }

    @PutMapping("/usuarios/{usuarioId}/tipo")
    @PreAuthorize("@securityMode.permitOrHasAdmin(authentication)")
    public UsuarioAdminResposta alterarTipoUsuario(
            @PathVariable Long usuarioId,
            @RequestParam TipoUsuario novoTipo,
            @RequestParam Long usuarioSolicitanteId) {

        try {

            controleAcessoService.exigirAdministrador(
                    usuarioSolicitanteId
            );

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioSolicitanteId,
                        "ALTERAR_TIPO_USUARIO",
                        "USUARIO",
                        usuarioId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
            }

            throw e;
        }

        Usuario usuario =
                adminService.alterarTipoUsuario(
                        usuarioId,
                        novoTipo,
                        usuarioSolicitanteId
                );

        return new UsuarioAdminResposta(usuario);
    }

    @GetMapping("/usuarios")
    @PreAuthorize("@securityMode.permitOrHasAdmin(authentication)")
    public List<UsuarioAdminResposta> listarUsuarios(
            @RequestParam Long usuarioSolicitanteId) {

        try {

            controleAcessoService.exigirAdministrador(
                    usuarioSolicitanteId
            );

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioSolicitanteId,
                        "LISTAR_USUARIOS_ADMIN",
                        "USUARIO",
                        null,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
            }

            throw e;
        }

        return adminService.listarUsuarios();
    }
}
