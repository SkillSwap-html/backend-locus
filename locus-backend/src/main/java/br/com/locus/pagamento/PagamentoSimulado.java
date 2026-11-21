package br.com.locus.pagamento;

import br.com.locus.reserva.ReservaSimulada;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos_simulados")
public class PagamentoSimulado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reserva_simulada_id", nullable = false, unique = true)
    private ReservaSimulada reservaSimulada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamentoSimulado status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataConclusao;

    private LocalDateTime dataInterrupcao;

    public PagamentoSimulado() {
    }

    public Long getId() {
        return id;
    }

    public ReservaSimulada getReservaSimulada() {
        return reservaSimulada;
    }

    public void setReservaSimulada(ReservaSimulada reservaSimulada) {
        this.reservaSimulada = reservaSimulada;
    }

    public StatusPagamentoSimulado getStatus() {
        return status;
    }

    public void setStatus(StatusPagamentoSimulado status) {
        this.status = status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public LocalDateTime getDataInterrupcao() {
        return dataInterrupcao;
    }

    public void setDataInterrupcao(LocalDateTime dataInterrupcao) {
        this.dataInterrupcao = dataInterrupcao;
    }
}