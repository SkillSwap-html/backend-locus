package br.com.locus.reserva;

import br.com.locus.orcamento.OrcamentoService;
import br.com.locus.orcamento.dto.ResumoCategoriaOrcamento;
import br.com.locus.orcamento.dto.ResumoOrcamento;
import br.com.locus.pagamento.PagamentoSimulado;
import br.com.locus.pagamento.PagamentoSimuladoRepository;
import br.com.locus.pagamento.StatusPagamentoSimulado;
import br.com.locus.perfil.TipoOrcamento;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReservaSimuladaService {

    private final ReservaSimuladaRepository reservaSimuladaRepository;
    private final ViagemRepository viagemRepository;
    private final OrcamentoService orcamentoService;
    private final PagamentoSimuladoRepository pagamentoSimuladoRepository;
    private final NotificacaoService notificacaoService;
    private final EventoAuditoriaService eventoAuditoriaService;
    private final ControleAcessoService controleAcessoService;

    public ReservaSimuladaService(
            ReservaSimuladaRepository reservaSimuladaRepository,
            ViagemRepository viagemRepository,
            OrcamentoService orcamentoService,
            PagamentoSimuladoRepository pagamentoSimuladoRepository,
            NotificacaoService notificacaoService,
            EventoAuditoriaService eventoAuditoriaService,
            ControleAcessoService controleAcessoService) {

        this.reservaSimuladaRepository = reservaSimuladaRepository;
        this.viagemRepository = viagemRepository;
        this.orcamentoService = orcamentoService;
        this.pagamentoSimuladoRepository = pagamentoSimuladoRepository;
        this.notificacaoService = notificacaoService;
        this.eventoAuditoriaService = eventoAuditoriaService;
        this.controleAcessoService = controleAcessoService;
    }

    public ReservaSimulada criar(Long viagemId) {

        Viagem viagem = viagemRepository.findById(viagemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Viagem não encontrada."
                        ));

        ReservaSimulada reservaExistente =
                reservaSimuladaRepository
                        .findFirstByViagemIdAndStatusNotOrderByIdDesc(
                                viagemId,
                                StatusReservaSimulada.CANCELADA_EM_SIMULACAO
                        )
                        .orElse(null);

        if (reservaExistente != null) {
            return reservaExistente;
        }

        ReservaSimulada reserva = new ReservaSimulada();

        reserva.setViagem(viagem);
        reserva.setProtocolo(gerarProtocolo());
        reserva.setStatus(
                StatusReservaSimulada.RASCUNHO
        );

        LocalDateTime agora =
                LocalDateTime.now();

        reserva.setDataCriacao(agora);
        reserva.setDataAtualizacao(agora);

        return reservaSimuladaRepository.save(
                reserva
        );
    }

    public ReservaSimulada prepararPagamento(Long reservaId) {

        ReservaSimulada reserva = buscarPorId(reservaId);

        if (reserva.getStatus()
                != StatusReservaSimulada.RASCUNHO) {

            throw new RuntimeException(
                    "Somente reservas em rascunho podem avançar para o pagamento simulado."
            );
        }

        ResumoOrcamento resumoOrcamento =
                orcamentoService.gerarResumo(
                        reserva.getViagem().getId()
                );

        verificarExcessoTotal(resumoOrcamento);
        verificarExcessoCategorias(resumoOrcamento);

        reserva.setValorTotalSimulado(
                resumoOrcamento.getTotalGasto()
        );

        reserva.setStatus(
                StatusReservaSimulada.AGUARDANDO_PAGAMENTO_SIMULADO
        );

        reserva.setDataAtualizacao(
                LocalDateTime.now()
        );

        ReservaSimulada reservaSalva =
                reservaSimuladaRepository.save(reserva);

        NotificacaoRequest notificacao =
                new NotificacaoRequest();

        notificacao.setTipo(TipoNotificacao.RESERVA);
        notificacao.setTitulo("Reserva aguardando pagamento");
        notificacao.setMensagem(
                "A reserva simulada " + reserva.getProtocolo()
                        + " está aguardando o pagamento simulado."
        );

        notificacaoService.criar(
                reserva.getViagem().getUsuario().getId(),
                notificacao
        );

        return reservaSalva;
    }

    @Transactional
    public ReservaSimulada confirmar(Long reservaId) {

        Long usuarioId = null;

        try {

            ReservaSimulada reserva = buscarPorId(reservaId);

            usuarioId =
                    reserva.getViagem()
                            .getUsuario()
                            .getId();

            if (reserva.getStatus()
                    == StatusReservaSimulada.CONFIRMADA_EM_SIMULACAO) {

                return reserva;
            }

            if (reserva.getStatus()
                    != StatusReservaSimulada.PAGAMENTO_SIMULADO_CONCLUIDO) {

                throw new RuntimeException(
                        "A reserva só pode ser confirmada após a conclusão do pagamento simulado."
                );
            }

            LocalDateTime agora = LocalDateTime.now();

            reserva.setStatus(
                    StatusReservaSimulada.CONFIRMADA_EM_SIMULACAO
            );

            reserva.setDataConfirmacao(agora);
            reserva.setDataAtualizacao(agora);

            ReservaSimulada reservaSalva =
                    reservaSimuladaRepository.save(reserva);

            NotificacaoRequest notificacao =
                    new NotificacaoRequest();

            notificacao.setTipo(TipoNotificacao.RESERVA);
            notificacao.setTitulo("Reserva simulada confirmada");
            notificacao.setMensagem(
                    "A reserva simulada " + reserva.getProtocolo()
                            + " foi confirmada com sucesso."
            );

            notificacaoService.criar(
                    usuarioId,
                    notificacao
            );

            eventoAuditoriaService.registrar(
                    usuarioId,
                    "CONFIRMAR_RESERVA_SIMULADA",
                    "RESERVA",
                    reserva.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Reserva simulada confirmada. Protocolo: "
                            + reserva.getProtocolo()
            );

            return reservaSalva;

        } catch (RuntimeException e) {

            try {
                eventoAuditoriaService.registrarFalha(
                        usuarioId,
                        "CONFIRMAR_RESERVA_SIMULADA",
                        "RESERVA",
                        reservaId,
                        e.getMessage()
                );
            } catch (RuntimeException erroAuditoria) {
                // Mantém o erro original da operação.
            }

            throw e;
        }
    }

    @Transactional
    public ReservaSimulada cancelar(Long reservaId) {

        Long usuarioId = null;

        try {

            ReservaSimulada reserva = buscarPorId(reservaId);

            usuarioId =
                    reserva.getViagem()
                            .getUsuario()
                            .getId();

            if (reserva.getStatus()
                    == StatusReservaSimulada.CANCELADA_EM_SIMULACAO) {

                throw new RuntimeException(
                        "A reserva simulada já está cancelada."
                );
            }

            PagamentoSimulado pagamento =
                    pagamentoSimuladoRepository
                            .findByReservaSimuladaId(reservaId)
                            .orElse(null);

            if (pagamento != null
                    && pagamento.getStatus()
                    == StatusPagamentoSimulado.INICIADO) {

                throw new RuntimeException(
                        "Interrompa o pagamento simulado antes de cancelar a reserva."
                );
            }

            LocalDateTime agora = LocalDateTime.now();

            reserva.setStatus(
                    StatusReservaSimulada.CANCELADA_EM_SIMULACAO
            );

            reserva.setDataCancelamento(agora);
            reserva.setDataAtualizacao(agora);

            ReservaSimulada reservaSalva =
                    reservaSimuladaRepository.save(reserva);

            NotificacaoRequest notificacao =
                    new NotificacaoRequest();

            notificacao.setTipo(TipoNotificacao.RESERVA);
            notificacao.setTitulo("Reserva simulada cancelada");
            notificacao.setMensagem(
                    "A reserva simulada " + reserva.getProtocolo()
                            + " foi cancelada."
            );

            notificacaoService.criar(
                    usuarioId,
                    notificacao
            );

            eventoAuditoriaService.registrar(
                    usuarioId,
                    "CANCELAR_RESERVA_SIMULADA",
                    "RESERVA",
                    reserva.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Reserva simulada cancelada. Protocolo: "
                            + reserva.getProtocolo()
            );

            return reservaSalva;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioId,
                        "CANCELAR_RESERVA_SIMULADA",
                        "RESERVA",
                        reservaId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
                // Mantém o erro original da operação.
            }

            throw e;
        }
    }

    public ReservaSimulada buscarPorId(Long reservaId) {

        return reservaSimuladaRepository.findById(reservaId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Reserva simulada não encontrada."
                        ));
    }

    public List<ReservaSimulada> listarPorViagem(Long viagemId) {

        if (!viagemRepository.existsById(viagemId)) {

            throw new RuntimeException(
                    "Viagem não encontrada."
            );
        }

        return reservaSimuladaRepository
                .findByViagemId(viagemId);
    }

    public ReservaSimulada criar(
            Long viagemId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return criar(viagemId);
    }

    public ReservaSimulada prepararPagamento(
            Long reservaId,
            Long usuarioSolicitanteId) {

        ReservaSimulada reserva = buscarPorId(reservaId);

        controleAcessoService.exigirProprietarioViagem(
                reserva.getViagem().getId(),
                usuarioSolicitanteId
        );

        return prepararPagamento(reservaId);
    }

    public ReservaSimulada confirmar(
            Long reservaId,
            Long usuarioSolicitanteId) {

        ReservaSimulada reserva = buscarPorId(reservaId);

        controleAcessoService.exigirProprietarioViagem(
                reserva.getViagem().getId(),
                usuarioSolicitanteId
        );

        return confirmar(reservaId);
    }

    public ReservaSimulada cancelar(
            Long reservaId,
            Long usuarioSolicitanteId) {

        ReservaSimulada reserva = buscarPorId(reservaId);

        controleAcessoService.exigirProprietarioViagem(
                reserva.getViagem().getId(),
                usuarioSolicitanteId
        );

        return cancelar(reservaId);
    }

    public ReservaSimulada buscarPorId(
            Long reservaId,
            Long usuarioSolicitanteId) {

        ReservaSimulada reserva = buscarPorId(reservaId);

        controleAcessoService.exigirProprietarioViagem(
                reserva.getViagem().getId(),
                usuarioSolicitanteId
        );

        return reserva;
    }

    public List<ReservaSimulada> listarPorViagem(
            Long viagemId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return listarPorViagem(viagemId);
    }

    private void verificarExcessoTotal(
            ResumoOrcamento resumoOrcamento) {

        if (resumoOrcamento.getTipoOrcamento()
                == TipoOrcamento.FIXO
                && resumoOrcamento.getExcessoTotal() != null
                && resumoOrcamento.getExcessoTotal()
                .compareTo(BigDecimal.ZERO) > 0) {

            throw new RuntimeException(
                    "O orçamento total foi ultrapassado em R$ "
                            + resumoOrcamento.getExcessoTotal()
                            + ". Revise o orçamento antes de prosseguir."
            );
        }
    }

    private void verificarExcessoCategorias(
            ResumoOrcamento resumoOrcamento) {

        for (ResumoCategoriaOrcamento categoria :
                resumoOrcamento.getCategorias()) {

            if (categoria.getExcesso() != null
                    && categoria.getExcesso()
                    .compareTo(BigDecimal.ZERO) > 0) {

                throw new RuntimeException(
                        "O limite da categoria "
                                + categoria.getCategoria()
                                + " foi ultrapassado em R$ "
                                + categoria.getExcesso()
                                + ". Revise o orçamento antes de prosseguir."
                );
            }
        }
    }

    private String gerarProtocolo() {

        String protocolo;

        do {

            protocolo = "LOCUS-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

        } while (reservaSimuladaRepository
                .existsByProtocolo(protocolo));

        return protocolo;
    }
}