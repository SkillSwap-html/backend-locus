package br.com.locus.compartilhamento.dto;

import br.com.locus.roteiro.ItemRoteiro;

import java.time.LocalTime;

public class ItemRoteiroCompartilhadoResposta {

    private Long id;
    private String titulo;
    private String nomeLugar;
    private LocalTime horarioInicio;
    private Integer duracaoMinutos;
    private Integer ordem;
    private Double custoEstimado;
    private String observacoes;
    private String motivoRecomendacao;

    public ItemRoteiroCompartilhadoResposta() {
    }

    public ItemRoteiroCompartilhadoResposta(ItemRoteiro item) {

        this.id = item.getId();
        this.titulo = item.getTitulo();
        this.horarioInicio = item.getHorarioInicio();
        this.duracaoMinutos = item.getDuracaoMinutos();
        this.ordem = item.getOrdem();
        this.custoEstimado = item.getCustoEstimado();
        this.observacoes = item.getObservacoes();
        this.motivoRecomendacao = item.getMotivoRecomendacao();

        if (item.getLugar() != null) {
            this.nomeLugar = item.getLugar().getNome();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}