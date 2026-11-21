package br.com.locus.relatorio.dto;

import br.com.locus.relatorio.RelatorioViagem;

import java.time.LocalDateTime;

public class RelatorioViagemResposta {

    private Long id;
    private Long reservaId;
    private String protocoloReserva;
    private String nomeArquivo;
    private LocalDateTime dataGeracao;

    public RelatorioViagemResposta() {
    }

    public RelatorioViagemResposta(RelatorioViagem relatorio) {

        this.id = relatorio.getId();

        if (relatorio.getReservaSimulada() != null) {

            this.reservaId =
                    relatorio.getReservaSimulada().getId();

            this.protocoloReserva =
                    relatorio.getReservaSimulada().getProtocolo();
        }

        this.nomeArquivo = relatorio.getNomeArquivo();
        this.dataGeracao = relatorio.getDataGeracao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public String getProtocoloReserva() {
        return protocoloReserva;
    }

    public void setProtocoloReserva(String protocoloReserva) {
        this.protocoloReserva = protocoloReserva;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }
}