package br.com.locus.roteiro.dto;

import br.com.locus.roteiro.DeslocamentoRoteiro;
import br.com.locus.roteiro.FonteEstimativaDeslocamento;

public class DeslocamentoRoteiroResposta {

    private Long id;
    private Long itemOrigemId;
    private Long itemDestinoId;
    private Integer duracaoMinutos;
    private Integer margemMinutos;
    private FonteEstimativaDeslocamento fonteEstimativa;

    public DeslocamentoRoteiroResposta(
            DeslocamentoRoteiro deslocamento) {

        this.id = deslocamento.getId();
        this.itemOrigemId =
                deslocamento.getItemOrigem().getId();
        this.itemDestinoId =
                deslocamento.getItemDestino().getId();
        this.duracaoMinutos =
                deslocamento.getDuracaoMinutos();
        this.margemMinutos =
                deslocamento.getMargemMinutos();
        this.fonteEstimativa =
                deslocamento.getFonteEstimativa();
    }

    public Long getId() {
        return id;
    }

    public Long getItemOrigemId() {
        return itemOrigemId;
    }

    public Long getItemDestinoId() {
        return itemDestinoId;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public Integer getMargemMinutos() {
        return margemMinutos;
    }

    public FonteEstimativaDeslocamento getFonteEstimativa() {
        return fonteEstimativa;
    }
}