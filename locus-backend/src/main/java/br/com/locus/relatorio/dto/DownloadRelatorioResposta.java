package br.com.locus.relatorio.dto;

import br.com.locus.relatorio.DownloadRelatorio;

import java.time.LocalDateTime;

public class DownloadRelatorioResposta {

    private Long id;
    private Long relatorioId;
    private String nomeArquivo;
    private LocalDateTime dataDownload;

    public DownloadRelatorioResposta() {
    }

    public DownloadRelatorioResposta(DownloadRelatorio download) {

        this.id = download.getId();
        this.dataDownload = download.getDataDownload();

        if (download.getRelatorioViagem() != null) {
            this.relatorioId =
                    download.getRelatorioViagem().getId();

            this.nomeArquivo =
                    download.getRelatorioViagem().getNomeArquivo();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRelatorioId() {
        return relatorioId;
    }

    public void setRelatorioId(Long relatorioId) {
        this.relatorioId = relatorioId;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public LocalDateTime getDataDownload() {
        return dataDownload;
    }

    public void setDataDownload(LocalDateTime dataDownload) {
        this.dataDownload = dataDownload;
    }
}