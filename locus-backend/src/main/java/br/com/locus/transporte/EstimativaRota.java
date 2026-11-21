package br.com.locus.transporte;

import br.com.locus.perfil.ModoTransporte;
import br.com.locus.viagem.Viagem;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estimativas_rota")
public class EstimativaRota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagem viagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModoTransporte modoTransporte;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal distanciaKm;

    @Column(nullable = false)
    private Integer duracaoMinutos;

    @Column(precision = 12, scale = 2)
    private BigDecimal custoCombustivel;

    @Column(precision = 12, scale = 2)
    private BigDecimal custoPedagios;

    @Column(precision = 12, scale = 2)
    private BigDecimal custoEstacionamento;

    @Column(precision = 12, scale = 2)
    private BigDecimal custoTotalEstimado;

    private Integer quantidadePassageiros;

    @Column(precision = 12, scale = 2)
    private BigDecimal custoPorPassageiro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FonteEstimativaRota fonteEstimativa;

    @Column(nullable = false)
    private LocalDateTime dataConsulta;

    public EstimativaRota() {
    }

    public Long getId() {
        return id;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
    }

    public ModoTransporte getModoTransporte() {
        return modoTransporte;
    }

    public void setModoTransporte(ModoTransporte modoTransporte) {
        this.modoTransporte = modoTransporte;
    }

    public BigDecimal getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(BigDecimal distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public BigDecimal getCustoCombustivel() {
        return custoCombustivel;
    }

    public void setCustoCombustivel(BigDecimal custoCombustivel) {
        this.custoCombustivel = custoCombustivel;
    }

    public BigDecimal getCustoPedagios() {
        return custoPedagios;
    }

    public void setCustoPedagios(BigDecimal custoPedagios) {
        this.custoPedagios = custoPedagios;
    }

    public BigDecimal getCustoEstacionamento() {
        return custoEstacionamento;
    }

    public void setCustoEstacionamento(BigDecimal custoEstacionamento) {
        this.custoEstacionamento = custoEstacionamento;
    }

    public BigDecimal getCustoTotalEstimado() {
        return custoTotalEstimado;
    }

    public void setCustoTotalEstimado(BigDecimal custoTotalEstimado) {
        this.custoTotalEstimado = custoTotalEstimado;
    }

    public Integer getQuantidadePassageiros() {
        return quantidadePassageiros;
    }

    public void setQuantidadePassageiros(Integer quantidadePassageiros) {
        this.quantidadePassageiros = quantidadePassageiros;
    }

    public BigDecimal getCustoPorPassageiro() {
        return custoPorPassageiro;
    }

    public void setCustoPorPassageiro(BigDecimal custoPorPassageiro) {
        this.custoPorPassageiro = custoPorPassageiro;
    }

    public FonteEstimativaRota getFonteEstimativa() {
        return fonteEstimativa;
    }

    public void setFonteEstimativa(
            FonteEstimativaRota fonteEstimativa) {
        this.fonteEstimativa = fonteEstimativa;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDateTime dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    @Column(precision = 10, scale = 2)
    private BigDecimal precoCombustivelLitro;

    @Column(precision = 10, scale = 2)
    private BigDecimal consumoMedioUtilizado;

    public BigDecimal getPrecoCombustivelLitro() {
        return precoCombustivelLitro;
    }

    public void setPrecoCombustivelLitro(BigDecimal precoCombustivelLitro) {
        this.precoCombustivelLitro = precoCombustivelLitro;
    }

    public BigDecimal getConsumoMedioUtilizado() {
        return consumoMedioUtilizado;
    }

    public void setConsumoMedioUtilizado(BigDecimal consumoMedioUtilizado) {
        this.consumoMedioUtilizado = consumoMedioUtilizado;
    }
}