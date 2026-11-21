package br.com.locus.notificacao;

import br.com.locus.notificacao.dto.NotificacaoRequest;
import br.com.locus.notificacao.dto.NotificacaoResposta;
import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.locus.exception.AcessoNegadoException;
import br.com.locus.admin.ControleAcessoService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ControleAcessoService controleAcessoService;

    public NotificacaoService(
            NotificacaoRepository notificacaoRepository,
            UsuarioRepository usuarioRepository,
            ControleAcessoService controleAcessoService) {

        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.controleAcessoService = controleAcessoService;
    }

    @Transactional
    public NotificacaoResposta criar(
            Long usuarioId,
            NotificacaoRequest request) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        if (request.getTipo() == null) {
            throw new RuntimeException(
                    "O tipo da notificação é obrigatório."
            );
        }

        if (request.getTitulo() == null ||
                request.getTitulo().isBlank()) {

            throw new RuntimeException(
                    "O título da notificação é obrigatório."
            );
        }

        if (request.getMensagem() == null ||
                request.getMensagem().isBlank()) {

            throw new RuntimeException(
                    "A mensagem da notificação é obrigatória."
            );
        }

        Notificacao notificacao = new Notificacao();

        notificacao.setUsuario(usuario);
        notificacao.setTipo(request.getTipo());
        notificacao.setTitulo(request.getTitulo());
        notificacao.setMensagem(request.getMensagem());
        notificacao.setLida(false);

        notificacaoRepository.save(notificacao);

        return new NotificacaoResposta(notificacao);
    }

    public List<NotificacaoResposta> listarPorUsuario(
            Long usuarioId) {

        validarUsuario(usuarioId);

        return notificacaoRepository
                .findByUsuarioIdOrderByDataCriacaoDesc(usuarioId)
                .stream()
                .map(NotificacaoResposta::new)
                .toList();
    }

    public List<NotificacaoResposta> listarNaoLidas(
            Long usuarioId) {

        validarUsuario(usuarioId);

        return notificacaoRepository
                .findByUsuarioIdAndLidaFalseOrderByDataCriacaoDesc(usuarioId)
                .stream()
                .map(NotificacaoResposta::new)
                .toList();
    }

    @Transactional
    public NotificacaoResposta marcarComoLida(
            Long notificacaoId,
            Long usuarioId) {

        Notificacao notificacao =
                notificacaoRepository.findById(notificacaoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notificação não encontrada."
                                )
                        );

        if (!notificacao.getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new AcessoNegadoException(
                    "Usuário não possui permissão para acessar esta notificação."
            );
        }

        if (!notificacao.isLida()) {
            notificacao.setLida(true);
            notificacao.setDataLeitura(LocalDateTime.now());

            notificacaoRepository.save(notificacao);
        }

        return new NotificacaoResposta(notificacao);
    }

    public long contarNaoLidas(Long usuarioId) {

        validarUsuario(usuarioId);

        return notificacaoRepository
                .countByUsuarioIdAndLidaFalse(usuarioId);
    }

    @Transactional
    public void marcarTodasComoLidas(Long usuarioId) {

        validarUsuario(usuarioId);

        List<Notificacao> notificacoes =
                notificacaoRepository
                        .findByUsuarioIdAndLidaFalseOrderByDataCriacaoDesc(
                                usuarioId
                        );

        LocalDateTime dataLeitura = LocalDateTime.now();

        for (Notificacao notificacao : notificacoes) {
            notificacao.setLida(true);
            notificacao.setDataLeitura(dataLeitura);
        }

        notificacaoRepository.saveAll(notificacoes);
    }

    public List<NotificacaoResposta> listarPorUsuario(
            Long usuarioId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirMesmoUsuario(
                usuarioId,
                usuarioSolicitanteId
        );

        return listarPorUsuario(usuarioId);
    }

    public List<NotificacaoResposta> listarNaoLidas(
            Long usuarioId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirMesmoUsuario(
                usuarioId,
                usuarioSolicitanteId
        );

        return listarNaoLidas(usuarioId);
    }

    public long contarNaoLidas(
            Long usuarioId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirMesmoUsuario(
                usuarioId,
                usuarioSolicitanteId
        );

        return contarNaoLidas(usuarioId);
    }

    @Transactional
    public void marcarTodasComoLidas(
            Long usuarioId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirMesmoUsuario(
                usuarioId,
                usuarioSolicitanteId
        );

        marcarTodasComoLidas(usuarioId);
    }

    @Transactional
    public NotificacaoResposta marcarComoLida(
            Long notificacaoId,
            Long usuarioId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirMesmoUsuario(
                usuarioId,
                usuarioSolicitanteId
        );

        return marcarComoLida(
                notificacaoId,
                usuarioId
        );
    }

    private void validarUsuario(Long usuarioId) {

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException(
                    "Usuário não encontrado."
            );
        }
    }
}