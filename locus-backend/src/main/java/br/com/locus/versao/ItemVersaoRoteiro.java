package br.com.locus.versao;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "itens_versao_roteiro")
public class ItemVersaoRoteiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "versao_roteiro_id", nullable = false)
    private VersaoRoteiro versaoRoteiro;

    @Column(nullable = false)
    private LocalDate dataDia;

    @Column(nullable = false)
    private String titulo;

    private String nomeLugar;

    private LocalTime horarioInicio;

    private Integer duracaoMinutos;

    private Integer ordem;

    private Double custoEstimado;

    private String observacoes;

    private String motivoRecomendacao;

    private Boolean favorito;

    public ItemVersaoRoteiro() {
    }

    public Long getId() {
        return id;
    }

    public VersaoRoteiro getVersaoRoteiro() {
        return versaoRoteiro;
    }

    public void setVersaoRoteiro(VersaoRoteiro versaoRoteiro) {
        this.versaoRoteiro = versaoRoteiro;
    }

    public LocalDate getDataDia() {
        return dataDia;
    }

    public void setDataDia(LocalDate dataDia) {
        this.dataDia = dataDia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNomeLugar() {
        return nomeLugar;
    }

    public void setNomeLugar(String nomeLugar) {
        this.nomeLugar = nomeLugar;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Double getCustoEstimado() {
        return custoEstimado;
    }

    public void setCustoEstimado(Double custoEstimado) {
        this.custoEstimado = custoEstimado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getMotivoRecomendacao() {
        return motivoRecomendacao;
    }

    public void setMotivoRecomendacao(String motivoRecomendacao) {
        this.motivoRecomendacao = motivoRecomendacao;
    }

    public Boolean getFavorito() {
        return favorito;
    }

    public void setFavorito(Boolean favorito) {
        this.favorito = favorito;
    }
}