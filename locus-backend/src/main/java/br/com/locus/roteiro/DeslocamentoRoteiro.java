package br.com.locus.roteiro;

import jakarta.persistence.*;

@Entity
@Table(name = "deslocamentos_roteiro")
public class DeslocamentoRoteiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_origem_id", nullable = false)
    private ItemRoteiro itemOrigem;

    @ManyToOne
    @JoinColumn(name = "item_destino_id", nullable = false)
    private ItemRoteiro itemDestino;

    private Integer duracaoMinutos;

    private Integer margemMinutos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FonteEstimativaDeslocamento fonteEstimativa;

    public DeslocamentoRoteiro() {
    }

    public Long getId() {
        return id;
    }

    public ItemRoteiro getItemOrigem() {
        return itemOrigem;
    }

    public void setItemOrigem(ItemRoteiro itemOrigem) {
        this.itemOrigem = itemOrigem;
    }

    public ItemRoteiro getItemDestino() {
        return itemDestino;
    }

    public void setItemDestino(ItemRoteiro itemDestino) {
        this.itemDestino = itemDestino;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public Integer getMargemMinutos() {
        return margemMinutos;
    }

    public void setMargemMinutos(Integer margemMinutos) {
        this.margemMinutos = margemMinutos;
    }

    public FonteEstimativaDeslocamento getFonteEstimativa() {
        return fonteEstimativa;
    }

    public void setFonteEstimativa(
            FonteEstimativaDeslocamento fonteEstimativa) {
        this.fonteEstimativa = fonteEstimativa;
    }
}