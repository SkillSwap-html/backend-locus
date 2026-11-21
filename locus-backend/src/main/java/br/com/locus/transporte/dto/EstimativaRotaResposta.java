package br.com.locus.transporte.dto;

import br.com.locus.perfil.ModoTransporte;
import br.com.locus.transporte.EstimativaRota;
import br.com.locus.transporte.FonteEstimativaRota;
import br.com.locus.viagem.dto.ViagemResposta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EstimativaRotaResposta {

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
    private ViagemResposta viagem;

    public EstimativaRotaResposta(EstimativaRota estimativaRota) {
        this.id = estimativaRota.getId();
        this.modoTransporte = estimativaRota.getModoTransporte();
        this.distanciaKm = estimativaRota.getDistanciaKm();
        this.duracaoMinutos = estimativaRota.getDuracaoMinutos();
        this.custoCombustivel = estimativaRota.getCustoCombustivel();
        this.custoPedagios = estimativaRota.getCustoPedagios();
        this.custoEstacionamento = estimativaRota.getCustoEstacionamento();
        this.custoTotalEstimado = estimativaRota.getCustoTotalEstimado();
        this.quantidadePassageiros = estimativaRota.getQuantidadePassageiros();
        this.custoPorPassageiro = estimativaRota.getCustoPorPassageiro();
        this.fonteEstimativa = estimativaRota.getFonteEstimativa();
        this.dataConsulta = estimativaRota.getDataConsulta();
        this.precoCombustivelLitro = estimativaRota.getPrecoCombustivelLitro();
        this.consumoMedioUtilizado = estimativaRota.getConsumoMedioUtilizado();
        this.viagem = new ViagemResposta(estimativaRota.getViagem());
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

    public ViagemResposta getViagem() {
        return viagem;
    }
}