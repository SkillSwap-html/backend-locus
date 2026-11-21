package br.com.locus.compartilhamento.dto;

import br.com.locus.perfil.ModoTransporte;
import br.com.locus.transporte.EstimativaRota;
import br.com.locus.transporte.FonteEstimativaRota;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EstimativaRotaCompartilhadaResposta {

    private Long id;
    private ModoTransporte modoTransporte;
    private BigDecimal distanciaKm;
    private Integer duracaoMinutos;

    private BigDecimal custoCombustivel;
    private BigDecimal custoPedagios;
    private BigDecimal custoEstacionamento;
    private BigDecimal custoTotalEstimado;

    private Integer quantidadePassageiros;
    private BigDecimal custoPorPassageiro;

    private FonteEstimativaRota fonteEstimativa;
    private LocalDateTime dataConsulta;

    private BigDecimal precoCombustivelLitro;
    private BigDecimal consumoMedioUtilizado;

    public EstimativaRotaCompartilhadaResposta() {
    }

    public EstimativaRotaCompartilhadaResposta(
            EstimativaRota estimativa) {

        this.id = estimativa.getId();
        this.modoTransporte = estimativa.getModoTransporte();
        this.distanciaKm = estimativa.getDistanciaKm();
        this.duracaoMinutos = estimativa.getDuracaoMinutos();

        this.custoCombustivel = estimativa.getCustoCombustivel();
        this.custoPedagios = estimativa.getCustoPedagios();
        this.custoEstacionamento = estimativa.getCustoEstacionamento();
        this.custoTotalEstimado = estimativa.getCustoTotalEstimado();

        this.quantidadePassageiros =
                estimativa.getQuantidadePassageiros();

        this.custoPorPassageiro =
                estimativa.getCustoPorPassageiro();

        this.fonteEstimativa =
                estimativa.getFonteEstimativa();

        this.dataConsulta =
                estimativa.getDataConsulta();

        this.precoCombustivelLitro =
                estimativa.getPrecoCombustivelLitro();

        this.consumoMedioUtilizado =
                estimativa.getConsumoMedioUtilizado();
    }

    public Long getId() {
        return id;
    }

    public ModoTransporte getModoTransporte() {
        return modoTransporte;
    }

    public BigDecimal getDistanciaKm() {
        return distanciaKm;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public BigDecimal getCustoCombustivel() {
        return custoCombustivel;
    }

    public BigDecimal getCustoPedagios() {
        return custoPedagios;
    }

    public BigDecimal getCustoEstacionamento() {
        return custoEstacionamento;
    }

    public BigDecimal getCustoTotalEstimado() {
        return custoTotalEstimado;
    }

    public Integer getQuantidadePassageiros() {
        return quantidadePassageiros;
    }

    public BigDecimal getCustoPorPassageiro() {
        return custoPorPassageiro;
    }

    public FonteEstimativaRota getFonteEstimativa() {
        return fonteEstimativa;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public BigDecimal getPrecoCombustivelLitro() {
        return precoCombustivelLitro;
    }

    public BigDecimal getConsumoMedioUtilizado() {
        return consumoMedioUtilizado;
    }
}