package br.com.locus.reserva.dto;

import br.com.locus.reserva.ReservaSimulada;
import br.com.locus.reserva.StatusReservaSimulada;
import br.com.locus.viagem.dto.ViagemResposta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservaSimuladaResposta {

    private Long id;
    private String protocolo;
    private StatusReservaSimulada status;
    private BigDecimal valorTotalSimulado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataConfirmacao;
    private LocalDateTime dataCancelamento;
    private ViagemResposta viagem;

    public ReservaSimuladaResposta(ReservaSimulada reservaSimulada) {
        this.id = reservaSimulada.getId();
        this.protocolo = reservaSimulada.getProtocolo();
        this.status = reservaSimulada.getStatus();
        this.valorTotalSimulado = reservaSimulada.getValorTotalSimulado();
        this.dataCriacao = reservaSimulada.getDataCriacao();
        this.dataAtualizacao = reservaSimulada.getDataAtualizacao();
        this.dataConfirmacao = reservaSimulada.getDataConfirmacao();
        this.dataCancelamento = reservaSimulada.getDataCancelamento();
        this.viagem = new ViagemResposta(reservaSimulada.getViagem());
    }

    public Long getId() {
        return id;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public StatusReservaSimulada getStatus() {
        return status;
    }

    public BigDecimal getValorTotalSimulado() {
        return valorTotalSimulado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public LocalDateTime getDataConfirmacao() {
        return dataConfirmacao;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public ViagemResposta getViagem() {
        return viagem;
    }
}