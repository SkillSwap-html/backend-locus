package br.com.locus.compartilhamento;

import br.com.locus.exception.AcessoNegadoException;
import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.locus.notificacao.NotificacaoService;
import br.com.locus.notificacao.TipoNotificacao;
import br.com.locus.notificacao.dto.NotificacaoRequest;
import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.auditoria.ResultadoAuditoria;
import br.com.locus.admin.ControleAcessoService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermissaoCompartilhamentoService {

    private final PermissaoCompartilhamentoRepository permissaoRepository;
    private final ViagemRepository viagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;
    private final EventoAuditoriaService eventoAuditoriaService;
    private final ControleAcessoService controleAcessoService;

    public PermissaoCompartilhamentoService(
            PermissaoCompartilhamentoRepository permissaoRepository,
            ViagemRepository viagemRepository,
            UsuarioRepository usuarioRepository,
            NotificacaoService notificacaoService,
            EventoAuditoriaService eventoAuditoriaService,
            ControleAcessoService controleAcessoService) {

        this.permissaoRepository = permissaoRepository;
        this.viagemRepository = viagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoService = notificacaoService;
        this.eventoAuditoriaService = eventoAuditoriaService;
        this.controleAcessoService = controleAcessoService;
    }

    @Transactional
    public PermissaoCompartilhamento compartilhar(
            Long viagemId,
            Long usuarioId,
            TipoPermissao tipoPermissao,
            Long usuarioSolicitanteId) {

        try {

            validarProprietario(
                    viagemId,
                    usuarioSolicitanteId
            );

            Viagem viagem =
                    buscarViagem(viagemId);

            Usuario usuario =
                    buscarUsuario(usuarioId);

            if (viagem.getUsuario().getId().equals(usuarioId)) {
                throw new RuntimeException(
                        "O proprietário da viagem já possui acesso total."
                );
            }

            if (tipoPermissao == null) {
                throw new RuntimeException(
                        "O tipo de permissão deve ser informado."
                );
            }

            boolean jaPossuiPermissao =
                    permissaoRepository
                            .findByViagemIdAndUsuarioIdAndAtivoTrue(
                                    viagemId,
                                    usuarioId
                            )
                            .isPresent();

            if (jaPossuiPermissao) {
                throw new RuntimeException(
                        "Este usuário já possui uma permissão ativa para esta viagem."
                );
            }

            PermissaoCompartilhamento permissao =
                    new PermissaoCompartilhamento();

            permissao.setViagem(viagem);
            permissao.setUsuario(usuario);
            permissao.setTipoPermissao(tipoPermissao);
            permissao.setAtivo(true);
            permissao.setDataCriacao(LocalDateTime.now());
            permissao.setDataRevogacao(null);

            PermissaoCompartilhamento permissaoSalva =
                    permissaoRepository.save(permissao);

            NotificacaoRequest notificacao =
                    new NotificacaoRequest();

            notificacao.setTipo(
                    TipoNotificacao.COMPARTILHAMENTO
            );

            notificacao.setTitulo(
                    "Viagem compartilhada"
            );

            notificacao.setMensagem(
                    "Uma viagem foi compartilhada com você com permissão de "
                            + tipoPermissao + "."
            );

            notificacaoService.criar(
                    usuarioId,
                    notificacao
            );

            eventoAuditoriaService.registrar(
                    usuarioSolicitanteId,
                    "COMPARTILHAR_VIAGEM",
                    "COMPARTILHAMENTO",
                    permissaoSalva.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Viagem " + viagemId
                            + " compartilhada com o usuário "
                            + usuarioId
                            + " com permissão "
                            + tipoPermissao
            );

            return permissaoSalva;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioSolicitanteId,
                        "COMPARTILHAR_VIAGEM",
                        "VIAGEM",
                        viagemId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
                // Mantém o erro original da operação.
            }

            throw e;
        }
    }

    @Transactional
    public PermissaoCompartilhamento alterarPermissao(
            Long permissaoId,
            TipoPermissao novoTipo,
            Long usuarioSolicitanteId) {

        try {

            PermissaoCompartilhamento permissao =
                    buscarPorId(permissaoId);

            validarProprietario(
                    permissao.getViagem().getId(),
                    usuarioSolicitanteId
            );

            if (!Boolean.TRUE.equals(permissao.getAtivo())) {
                throw new RuntimeException(
                        "Não é possível alterar uma permissão revogada."
                );
            }

            if (novoTipo == null) {
                throw new RuntimeException(
                        "O novo tipo de permissão deve ser informado."
                );
            }

            permissao.setTipoPermissao(novoTipo);

            PermissaoCompartilhamento permissaoSalva =
                    permissaoRepository.save(permissao);

            NotificacaoRequest notificacao =
                    new NotificacaoRequest();

            notificacao.setTipo(
                    TipoNotificacao.COMPARTILHAMENTO
            );

            notificacao.setTitulo(
                    "Permissão de viagem alterada"
            );

            notificacao.setMensagem(
                    "Sua permissão em uma viagem compartilhada foi alterada para "
                            + novoTipo + "."
            );

            notificacaoService.criar(
                    permissao.getUsuario().getId(),
                    notificacao
            );

            eventoAuditoriaService.registrar(
                    usuarioSolicitanteId,
                    "ALTERAR_PERMISSAO_COMPARTILHAMENTO",
                    "COMPARTILHAMENTO",
                    permissaoSalva.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Permissão de compartilhamento alterada para "
                            + novoTipo
            );

            return permissaoSalva;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioSolicitanteId,
                        "ALTERAR_PERMISSAO_COMPARTILHAMENTO",
                        "COMPARTILHAMENTO",
                        permissaoId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
                // Mantém o erro original da operação.
            }

            throw e;
        }
    }

    @Transactional
    public PermissaoCompartilhamento revogar(
            Long permissaoId,
            Long usuarioSolicitanteId) {

        try {

            PermissaoCompartilhamento permissao =
                    buscarPorId(permissaoId);

            validarProprietario(
                    permissao.getViagem().getId(),
                    usuarioSolicitanteId
            );

            if (!Boolean.TRUE.equals(permissao.getAtivo())) {
                throw new RuntimeException(
                        "Esta permissão já foi revogada."
                );
            }

            permissao.setAtivo(false);
            permissao.setDataRevogacao(LocalDateTime.now());

            PermissaoCompartilhamento permissaoSalva =
                    permissaoRepository.save(permissao);

            NotificacaoRequest notificacao =
                    new NotificacaoRequest();

            notificacao.setTipo(
                    TipoNotificacao.COMPARTILHAMENTO
            );

            notificacao.setTitulo(
                    "Compartilhamento revogado"
            );

            notificacao.setMensagem(
                    "Seu acesso a uma viagem compartilhada foi revogado."
            );

            notificacaoService.criar(
                    permissao.getUsuario().getId(),
                    notificacao
            );

            eventoAuditoriaService.registrar(
                    usuarioSolicitanteId,
                    "REVOGAR_COMPARTILHAMENTO",
                    "COMPARTILHAMENTO",
                    permissaoSalva.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Compartilhamento da viagem "
                            + permissao.getViagem().getId()
                            + " com o usuário "
                            + permissao.getUsuario().getId()
                            + " foi revogado."
            );

            return permissaoSalva;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioSolicitanteId,
                        "REVOGAR_COMPARTILHAMENTO",
                        "COMPARTILHAMENTO",
                        permissaoId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
                // Mantém o erro original da operação.
            }

            throw e;
        }
    }

    public PermissaoCompartilhamento buscarPorId(Long permissaoId) {

        return permissaoRepository.findById(permissaoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Permissão de compartilhamento não encontrada."
                        )
                );
    }

    public List<PermissaoCompartilhamento> listarPorViagem(
            Long viagemId,
            Long usuarioSolicitanteId) {

        validarProprietario(viagemId, usuarioSolicitanteId);

        return permissaoRepository
                .findByViagemIdAndAtivoTrue(viagemId);
    }

    public List<PermissaoCompartilhamento> listarPorUsuario(
            Long usuarioId,
            Long usuarioSolicitanteId) {

        buscarUsuario(usuarioId);

        if (!usuarioId.equals(usuarioSolicitanteId)) {
            throw new AcessoNegadoException(
                    "Usuário não possui permissão para consultar os compartilhamentos de outro usuário."
            );
        }

        return permissaoRepository
                .findByUsuarioIdAndAtivoTrue(usuarioId);
    }

    public boolean podeVisualizar(
            Long viagemId,
            Long usuarioId) {

        controleAcessoService.exigirSolicitanteAutenticado(usuarioId);

        Viagem viagem = buscarViagem(viagemId);

        if (viagem.getUsuario().getId().equals(usuarioId)) {
            return true;
        }

        return permissaoRepository
                .findByViagemIdAndUsuarioIdAndAtivoTrue(
                        viagemId,
                        usuarioId
                )
                .isPresent();
    }

    public boolean podeEditar(
            Long viagemId,
            Long usuarioId) {

        controleAcessoService.exigirSolicitanteAutenticado(usuarioId);

        Viagem viagem = buscarViagem(viagemId);

        if (viagem.getUsuario().getId().equals(usuarioId)) {
            return true;
        }

        PermissaoCompartilhamento permissao =
                permissaoRepository
                        .findByViagemIdAndUsuarioIdAndAtivoTrue(
                                viagemId,
                                usuarioId
                        )
                        .orElse(null);

        if (permissao == null) {
            return false;
        }

        return permissao.getTipoPermissao()
                == TipoPermissao.EDICAO;
    }

    public void validarVisualizacao(
            Long viagemId,
            Long usuarioId) {

        if (!podeVisualizar(viagemId, usuarioId)) {
            throw new AcessoNegadoException(
                    "Usuário não possui permissão para visualizar esta viagem."
            );
        }
    }

    public void validarEdicao(
            Long viagemId,
            Long usuarioId) {

        if (!podeEditar(viagemId, usuarioId)) {
            throw new AcessoNegadoException(
                    "Usuário não possui permissão para editar esta viagem."
            );
        }
    }

    public void validarProprietario(
            Long viagemId,
            Long usuarioId) {

        controleAcessoService.exigirSolicitanteAutenticado(usuarioId);

        Viagem viagem = buscarViagem(viagemId);

        if (!viagem.getUsuario().getId().equals(usuarioId)) {
            throw new AcessoNegadoException(
                    "Somente o proprietário da viagem pode gerenciar compartilhamentos."
            );
        }
    }

    private Viagem buscarViagem(Long viagemId) {

        return viagemRepository.findById(viagemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Viagem não encontrada."
                        )
                );
    }

    private Usuario buscarUsuario(Long usuarioId) {

        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuário não encontrado."
                        )
                );
    }
}
