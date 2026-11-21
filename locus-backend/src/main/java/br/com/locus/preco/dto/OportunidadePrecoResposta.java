package br.com.locus.preco.dto;

import br.com.locus.preco.ClassificacaoPreco;

import java.math.BigDecimal;

public class OportunidadePrecoResposta {

    private BigDecimal precoAtual;
    private BigDecimal mediaHistorica;
    private ClassificacaoPreco classificacao;
    private String justificativa;

    public OportunidadePrecoResposta(
            BigDecimal precoAtual,
            BigDecimal mediaHistorica,
            ClassificacaoPreco classificacao,
            String justificativa) {

        this.precoAtual = precoAtual;
        this.mediaHistorica = mediaHistorica;
        this.classificacao = classificacao;
        this.justificativa = justificativa;
    }

    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }

    public BigDecimal getMediaHistorica() {
        return mediaHistorica;
    }

    public ClassificacaoPreco getClassificacao() {
        return classificacao;
    }

    public String getJustificativa() {
        return justificativa;
    }
}