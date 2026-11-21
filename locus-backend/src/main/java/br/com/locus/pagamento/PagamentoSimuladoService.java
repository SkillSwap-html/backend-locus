package br.com.locus.pagamento;

import br.com.locus.admin.ControleAcessoService;
import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.auditoria.ResultadoAuditoria;
import br.com.locus.notificacao.NotificacaoService;
import br.com.locus.notificacao.TipoNotificacao;
import br.com.locus.notificacao.dto.NotificacaoRequest;
import br.com.locus.reserva.ReservaSimulada;
import br.com.locus.reserva.ReservaSimuladaRepository;
import br.com.locus.reserva.StatusReservaSimulada;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PagamentoSimuladoService {

    private final PagamentoSimuladoRepository pagamentoSimuladoRepository;
    private final ReservaSimuladaRepository reservaSimuladaRepository;
    private final NotificacaoService notificacaoService;
    private final EventoAuditoriaService eventoAuditoriaService;
    private final ControleAcessoService controleAcessoService;

    public PagamentoSimuladoService(
            PagamentoSimuladoRepository pagamentoSimuladoRepository,
            ReservaSimuladaRepository reservaSimuladaRepository,
            NotificacaoService notificacaoService,
            EventoAuditoriaService eventoAuditoriaService,
            ControleAcessoService controleAcessoService) {

        this.pagamentoSimuladoRepository =
                pagamentoSimuladoRepository;

        this.reservaSimuladaRepository =
                reservaSimuladaRepository;

        this.notificacaoService =
                notificacaoService;

        this.eventoAuditoriaService =
                eventoAuditoriaService;

        this.controleAcessoService =
                controleAcessoService;
    }

    public PagamentoSimulado iniciar(Long reservaId) {

        ReservaSimulada reserva =
                reservaSimuladaRepository.findById(reservaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reserva simulada não encontrada."
                                ));

        if (reserva.getStatus()
                != StatusReservaSimulada
                .AGUARDANDO_PAGAMENTO_SIMULADO) {

            throw new RuntimeException(
                    "A reserva não está aguardando pagamento simulado."
            );
        }

        PagamentoSimulado pagamentoExistente =
                pagamentoSimuladoRepository
                        .findByReservaSimuladaId(reservaId)
                        .orElse(null);

        if (pagamentoExistente != null) {

            if (pagamentoExistente.getStatus()
                    == StatusPagamentoSimulado.CONCLUIDO) {
                return pagamentoExistente;
            }

            if (pagamentoExistente.getStatus()
                    == StatusPagamentoSimulado.INICIADO) {

                return pagamentoExistente;
            }

            pagamentoExistente.setStatus(
                    StatusPagamentoSimulado.INICIADO
            );

            pagamentoExistente.setDataInterrupcao(null);

            return pagamentoSimuladoRepository.save(
                    pagamentoExistente
            );
        }

        if (reserva.getValorTotalSimulado() == null) {
            throw new RuntimeException(
                    "A reserva não possui valor total simulado."
            );
        }

        PagamentoSimulado pagamento =
                new PagamentoSimulado();

        pagamento.setReservaSimulada(reserva);

        pagamento.setValor(
                reserva.getValorTotalSimulado()
        );

        pagamento.setStatus(
                StatusPagamentoSimulado.INICIADO
        );

        pagamento.setDataCriacao(
                LocalDateTime.now()
        );

        return pagamentoSimuladoRepository.save(
                pagamento
        );
    }

    @Transactional
    public PagamentoSimulado concluir(Long pagamentoId) {

        Long usuarioId = null;

        try {

            PagamentoSimulado pagamento =
                    buscarPorId(pagamentoId);

            ReservaSimulada reserva =
                    pagamento.getReservaSimulada();

            usuarioId =
                    reserva.getViagem()
                            .getUsuario()
                            .getId();

            if (pagamento.getStatus()
                    == StatusPagamentoSimulado.CONCLUIDO) {

                return pagamento;
            }

            if (pagamento.getStatus()
                    != StatusPagamentoSimulado.INICIADO) {

                throw new RuntimeException(
                        "Somente pagamentos iniciados podem ser concluídos."
                );
            }

            if (reserva.getStatus()
                    != StatusReservaSimulada.AGUARDANDO_PAGAMENTO_SIMULADO) {

                throw new RuntimeException(
                        "A reserva não está aguardando pagamento simulado."
                );
            }

            LocalDateTime agora =
                    LocalDateTime.now();

            pagamento.setStatus(
                    StatusPagamentoSimulado.CONCLUIDO
            );

            pagamento.setDataConclusao(agora);

            reserva.setStatus(
                    StatusReservaSimulada.PAGAMENTO_SIMULADO_CONCLUIDO
            );

            reserva.setDataAtualizacao(agora);

            reservaSimuladaRepository.save(reserva);

            PagamentoSimulado pagamentoSalvo =
                    pagamentoSimuladoRepository.save(pagamento);

            NotificacaoRequest notificacao =
                    new NotificacaoRequest();

            notificacao.setTipo(
                    TipoNotificacao.PAGAMENTO
            );

            notificacao.setTitulo(
                    "Pagamento simulado concluído"
            );

            notificacao.setMensagem(
                    "O pagamento simulado da reserva "
                            + reserva.getProtocolo()
                            + " foi concluído com sucesso."
            );

            notificacaoService.criar(
                    usuarioId,
                    notificacao
            );

            eventoAuditoriaService.registrar(
                    usuarioId,
                    "CONCLUIR_PAGAMENTO_SIMULADO",
                    "PAGAMENTO",
                    pagamento.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Pagamento simulado concluído para a reserva "
                            + reserva.getProtocolo()
            );

            return pagamentoSalvo;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioId,
                        "CONCLUIR_PAGAMENTO_SIMULADO",
                        "PAGAMENTO",
                        pagamentoId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
            }

            throw e;
        }
    }

    @Transactional
    public PagamentoSimulado interromper(Long pagamentoId) {

        Long usuarioId = null;

        try {

            PagamentoSimulado pagamento =
                    buscarPorId(pagamentoId);

            ReservaSimulada reserva =
                    pagamento.getReservaSimulada();

            usuarioId =
                    reserva.getViagem()
                            .getUsuario()
                            .getId();

            if (pagamento.getStatus() == StatusPagamentoSimulado.INTERROMPIDO) {
                return pagamento;
            }
            if (pagamento.getStatus() != StatusPagamentoSimulado.INICIADO) {
                throw new RuntimeException(
                        "Somente pagamentos iniciados podem ser interrompidos."
                );
            }

            pagamento.setStatus(
                    StatusPagamentoSimulado.INTERROMPIDO
            );

            pagamento.setDataInterrupcao(
                    LocalDateTime.now()
            );

            PagamentoSimulado pagamentoSalvo =
                    pagamentoSimuladoRepository.save(pagamento);

            eventoAuditoriaService.registrar(
                    usuarioId,
                    "INTERROMPER_PAGAMENTO_SIMULADO",
                    "PAGAMENTO",
                    pagamento.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Pagamento simulado interrompido para a reserva "
                            + reserva.getProtocolo()
            );

            return pagamentoSalvo;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioId,
                        "INTERROMPER_PAGAMENTO_SIMULADO",
                        "PAGAMENTO",
                        pagamentoId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
            }

            throw e;
        }
    }

    public PagamentoSimulado buscarPorReserva(Long reservaId) {

        return pagamentoSimuladoRepository
                .findByReservaSimuladaId(reservaId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Pagamento simulado não encontrado para esta reserva."
                        ));
    }

    public PagamentoSimulado buscarPorId(
            Long pagamentoId) {

        return pagamentoSimuladoRepository
                .findById(pagamentoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Pagamento simulado não encontrado."
                        ));
    }

    public PagamentoSimulado iniciar(
            Long reservaId,
            Long usuarioSolicitanteId) {

        ReservaSimulada reserva =
                reservaSimuladaRepository.findById(reservaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reserva simulada não encontrada."
                                ));

        controleAcessoService.exigirProprietarioViagem(
                reserva.getViagem().getId(),
                usuarioSolicitanteId
        );

        return iniciar(reservaId);
    }

    public PagamentoSimulado concluir(
            Long pagamentoId,
            Long usuarioSolicitanteId) {

        PagamentoSimulado pagamento =
                buscarPorId(pagamentoId);

        controleAcessoService.exigirProprietarioViagem(
                pagamento.getReservaSimulada()
                        .getViagem()
                        .getId(),
                usuarioSolicitanteId
        );

        return concluir(pagamentoId);
    }

    public PagamentoSimulado interromper(
            Long pagamentoId,
            Long usuarioSolicitanteId) {

        PagamentoSimulado pagamento =
                buscarPorId(pagamentoId);

        controleAcessoService.exigirProprietarioViagem(
                pagamento.getReservaSimulada()
                        .getViagem()
                        .getId(),
                usuarioSolicitanteId
        );

        return interromper(pagamentoId);
    }

    public PagamentoSimulado buscarPorReserva(
            Long reservaId,
            Long usuarioSolicitanteId) {

        ReservaSimulada reserva =
                reservaSimuladaRepository.findById(reservaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reserva simulada não encontrada."
                                ));

        controleAcessoService.exigirProprietarioViagem(
                reserva.getViagem().getId(),
                usuarioSolicitanteId
        );

        return buscarPorReserva(reservaId);
    }

    public PagamentoSimulado buscarPorId(
            Long pagamentoId,
            Long usuarioSolicitanteId) {

        PagamentoSimulado pagamento =
                buscarPorId(pagamentoId);

        controleAcessoService.exigirProprietarioViagem(
                pagamento.getReservaSimulada()
                        .getViagem()
                        .getId(),
                usuarioSolicitanteId
        );

        return pagamento;
    }
}
