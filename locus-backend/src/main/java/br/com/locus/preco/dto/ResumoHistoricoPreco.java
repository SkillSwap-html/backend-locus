package br.com.locus.preco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ResumoHistoricoPreco {

    private BigDecimal menorPreco;
    private BigDecimal maiorPreco;
    private BigDecimal mediaPreco;
    private BigDecimal medianaPreco;
    private BigDecimal precoMaisRecente;
    private BigDecimal variacaoPercentual;
    private int quantidadeObservacoes;
    private LocalDateTime dataMaisAntiga;
    private LocalDateTime dataMaisRecente;
    private boolean historicoSuficiente;

    public ResumoHistoricoPreco(
            BigDecimal menorPreco,
            BigDecimal maiorPreco,
            BigDecimal mediaPreco,
            BigDecimal medianaPreco,
            BigDecimal precoMaisRecente,
            BigDecimal variacaoPercentual,
            int quantidadeObservacoes,
            LocalDateTime dataMaisAntiga,
            LocalDateTime dataMaisRecente,
            boolean historicoSuficiente) {

        this.menorPreco = menorPreco;
        this.maiorPreco = maiorPreco;
        this.mediaPreco = mediaPreco;
        this.medianaPreco = medianaPreco;
        this.precoMaisRecente = precoMaisRecente;
        this.variacaoPercentual = variacaoPercentual;
        this.quantidadeObservacoes = quantidadeObservacoes;
        this.dataMaisAntiga = dataMaisAntiga;
        this.dataMaisRecente = dataMaisRecente;
        this.historicoSuficiente = historicoSuficiente;
    }

    public BigDecimal getMenorPreco() {
        return menorPreco;
    }

    public BigDecimal getMaiorPreco() {
        return maiorPreco;
    }

    public BigDecimal getMediaPreco() {
        return mediaPreco;
    }

    public BigDecimal getMedianaPreco() {
        return medianaPreco;
    }

    public BigDecimal getPrecoMaisRecente() {
        return precoMaisRecente;
    }

    public BigDecimal getVariacaoPercentual() {
        return variacaoPercentual;
    }

    public int getQuantidadeObservacoes() {
        return quantidadeObservacoes;
    }

    public LocalDateTime getDataMaisAntiga() {
        return dataMaisAntiga;
    }

    public LocalDateTime getDataMaisRecente() {
        return dataMaisRecente;
    }

    public boolean isHistoricoSuficiente() {
        return historicoSuficiente;
    }
}