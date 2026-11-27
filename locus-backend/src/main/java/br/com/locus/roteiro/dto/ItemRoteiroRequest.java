package br.com.locus.roteiro.dto;

import java.time.LocalTime;

public class ItemRoteiroRequest {

    private String titulo;
    private LocalTime horarioInicio;
    private Integer duracaoMinutos;
    private Double custoEstimado;
    private String observacoes;
    private String motivoRecomendacao;

    public ItemRoteiroRequest() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
}