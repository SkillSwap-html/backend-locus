package br.com.locus.versao.dto;

import br.com.locus.versao.ItemVersaoRoteiro;

import java.time.LocalDate;
import java.time.LocalTime;

public class ItemVersaoRoteiroResposta {

    private Long id;
    private Long versaoRoteiroId;
    private LocalDate dataDia;
    private String titulo;
    private String nomeLugar;
    private LocalTime horarioInicio;
    private Integer duracaoMinutos;
    private Integer ordem;
    private Double custoEstimado;
    private String observacoes;
    private String motivoRecomendacao;
    private Boolean favorito;

    public ItemVersaoRoteiroResposta(ItemVersaoRoteiro item) {
        this.id = item.getId();
        this.versaoRoteiroId = item.getVersaoRoteiro().getId();
        this.dataDia = item.getDataDia();
        this.titulo = item.getTitulo();
        this.nomeLugar = item.getNomeLugar();
        this.horarioInicio = item.getHorarioInicio();
        this.duracaoMinutos = item.getDuracaoMinutos();
        this.ordem = item.getOrdem();
        this.custoEstimado = item.getCustoEstimado();
        this.observacoes = item.getObservacoes();
        this.motivoRecomendacao = item.getMotivoRecomendacao();
        this.favorito = item.getFavorito();
    }

    public Long getId() {
        return id;
    }

    public Long getVersaoRoteiroId() {
        return versaoRoteiroId;
    }

    public LocalDate getDataDia() {
        return dataDia;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getNomeLugar() {
        return nomeLugar;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public Double getCustoEstimado() {
        return custoEstimado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public String getMotivoRecomendacao() {
        return motivoRecomendacao;
    }

    public Boolean getFavorito() {
        return favorito;
    }
}