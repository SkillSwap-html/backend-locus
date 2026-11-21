package br.com.locus.admin;

import br.com.locus.admin.dto.UsuarioAdminResposta;
import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.auditoria.ResultadoAuditoria;
import br.com.locus.usuario.TipoUsuario;
import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final EventoAuditoriaService eventoAuditoriaService;

    public AdminService(
            UsuarioRepository usuarioRepository,
            EventoAuditoriaService eventoAuditoriaService) {

        this.usuarioRepository = usuarioRepository;
        this.eventoAuditoriaService = eventoAuditoriaService;
    }

    @Transactional
    public Usuario alterarTipoUsuario(
            Long usuarioId,
            TipoUsuario novoTipo,
            Long usuarioSolicitanteId) {

        try {

            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() ->
                            new RuntimeException("Usuário não encontrado.")
                    );

            if (novoTipo == null) {
                throw new RuntimeException(
                        "O novo tipo de usuário é obrigatório."
                );
            }

            TipoUsuario tipoAnterior = usuario.getTipoUsuario();

            if (tipoAnterior == TipoUsuario.ADMINISTRADOR
                    && novoTipo != TipoUsuario.ADMINISTRADOR) {

                long quantidadeAdministradores =
                        usuarioRepository.countByTipoUsuario(
                                TipoUsuario.ADMINISTRADOR
                        );

                if (quantidadeAdministradores <= 1) {
                    throw new RuntimeException(
                            "Não é possível alterar o último administrador do sistema."
                    );
                }
            }

            usuario.setTipoUsuario(novoTipo);

            Usuario usuarioSalvo =
                    usuarioRepository.save(usuario);

            eventoAuditoriaService.registrar(
                    usuarioSolicitanteId,
                    "ALTERAR_TIPO_USUARIO",
                    "USUARIO",
                    usuarioSalvo.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Tipo do usuário alterado de "
                            + tipoAnterior
                            + " para "
                            + novoTipo
            );

            return usuarioSalvo;

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
    }

    public List<UsuarioAdminResposta> listarUsuarios() {

        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioAdminResposta::new)
                .toList();
    }
}