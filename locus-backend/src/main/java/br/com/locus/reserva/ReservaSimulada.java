package br.com.locus.reserva;

import br.com.locus.viagem.Viagem;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas_simuladas")
public class ReservaSimulada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagem viagem;

    @Column(nullable = false, unique = true)
    private String protocolo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReservaSimulada status;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorTotalSimulado;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    private LocalDateTime dataConfirmacao;

    private LocalDateTime dataCancelamento;

    public ReservaSimulada() {
    }

    public Long getId() {
        return id;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public StatusReservaSimulada getStatus() {
        return status;
    }

    public void setStatus(StatusReservaSimulada status) {
        this.status = status;
    }

    public BigDecimal getValorTotalSimulado() {
        return valorTotalSimulado;
    }

    public void setValorTotalSimulado(BigDecimal valorTotalSimulado) {
        this.valorTotalSimulado = valorTotalSimulado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public LocalDateTime getDataConfirmacao() {
        return dataConfirmacao;
    }

    public void setDataConfirmacao(LocalDateTime dataConfirmacao) {
        this.dataConfirmacao = dataConfirmacao;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(LocalDateTime dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }
}