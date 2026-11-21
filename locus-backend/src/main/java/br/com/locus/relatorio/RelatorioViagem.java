package br.com.locus.relatorio;

import br.com.locus.reserva.ReservaSimulada;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "relatorios_viagem")
public class RelatorioViagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(
            name = "reserva_simulada_id",
            nullable = false,
            unique = true
    )
    private ReservaSimulada reservaSimulada;

    @Column(nullable = false)
    private String nomeArquivo;

    @JsonIgnore
    @Column(
            name = "arquivo_pdf",
            nullable = false,
            columnDefinition = "bytea"
    )
    private byte[] arquivoPdf;

    @Column(nullable = false)
    private LocalDateTime dataGeracao;

    public RelatorioViagem() {
    }

    public Long getId() {
        return id;
    }

    public ReservaSimulada getReservaSimulada() {
        return reservaSimulada;
    }

    public void setReservaSimulada(
            ReservaSimulada reservaSimulada) {

        this.reservaSimulada = reservaSimulada;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public byte[] getArquivoPdf() {
        return arquivoPdf;
    }

    public void setArquivoPdf(byte[] arquivoPdf) {
        this.arquivoPdf = arquivoPdf;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(
            LocalDateTime dataGeracao) {

        this.dataGeracao = dataGeracao;
    }
}