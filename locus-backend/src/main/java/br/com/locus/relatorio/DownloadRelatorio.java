package br.com.locus.relatorio;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "downloads_relatorio")
public class DownloadRelatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "relatorio_viagem_id",
            nullable = false
    )
    private RelatorioViagem relatorioViagem;

    @Column(nullable = false)
    private LocalDateTime dataDownload;

    public DownloadRelatorio() {
    }

    public Long getId() {
        return id;
    }

    public RelatorioViagem getRelatorioViagem() {
        return relatorioViagem;
    }

    public void setRelatorioViagem(RelatorioViagem relatorioViagem) {
        this.relatorioViagem = relatorioViagem;
    }

    public LocalDateTime getDataDownload() {
        return dataDownload;
    }

    public void setDataDownload(LocalDateTime dataDownload) {
        this.dataDownload = dataDownload;
    }
}