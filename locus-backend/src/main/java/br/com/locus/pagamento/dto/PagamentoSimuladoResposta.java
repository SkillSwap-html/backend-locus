package br.com.locus.pagamento.dto;

import br.com.locus.pagamento.PagamentoSimulado;
import br.com.locus.pagamento.StatusPagamentoSimulado;
import br.com.locus.reserva.dto.ReservaSimuladaResposta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoSimuladoResposta {

    private Long id;
    private BigDecimal valor;
    private StatusPagamentoSimulado status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;
    private LocalDateTime dataInterrupcao;
    private ReservaSimuladaResposta reservaSimulada;

    public PagamentoSimuladoResposta(
            PagamentoSimulado pagamentoSimulado) {

        this.id = pagamentoSimulado.getId();
        this.valor = pagamentoSimulado.getValor();
        this.status = pagamentoSimulado.getStatus();
        this.dataCriacao = pagamentoSimulado.getDataCriacao();
        this.dataConclusao = pagamentoSimulado.getDataConclusao();
        this.dataInterrupcao = pagamentoSimulado.getDataInterrupcao();
        this.reservaSimulada =
                new ReservaSimuladaResposta(
                        pagamentoSimulado.getReservaSimulada()
                );
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public StatusPagamentoSimulado getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public LocalDateTime getDataInterrupcao() {
        return dataInterrupcao;
    }

    public ReservaSimuladaResposta getReservaSimulada() {
        return reservaSimulada;
    }
}