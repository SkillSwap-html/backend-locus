package br.com.locus.roteiro.dto;

import java.time.LocalTime;
import br.com.locus.roteiro.ItemRoteiro;

public class ItemRoteiroResposta {

    private Long id;
    private Long versao;
    private Long diaRoteiroId;
    private Long lugarId;
    private String nomeLugar;
    private String titulo;
    private LocalTime horarioInicio;
    private Integer duracaoMinutos;
    private Integer ordem;
    private Double custoEstimado;
    private String observacoes;
    private String motivoRecomendacao;
    private Boolean favorito;

    public ItemRoteiroResposta() {
    }

    public ItemRoteiroResposta(ItemRoteiro item) {

        this.id = item.getId();
        this.versao = item.getVersao();

        if (item.getDiaRoteiro() != null) {
            this.diaRoteiroId = item.getDiaRoteiro().getId();
        }

        if (item.getLugar() != null) {
            this.lugarId = item.getLugar().getId();
            this.nomeLugar = item.getLugar().getNome();
        }

        this.titulo = item.getTitulo();
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public Long getDiaRoteiroId() {
        return diaRoteiroId;
    }

    public void setDiaRoteiroId(Long diaRoteiroId) {
        this.diaRoteiroId = diaRoteiroId;
    }

    public Long getLugarId() {
        return lugarId;
    }

    public void setLugarId(Long lugarId) {
        this.lugarId = lugarId;
    }

    public String getNomeLugar() {
        return nomeLugar;
    }

    public void setNomeLugar(String nomeLugar) {
        this.nomeLugar = nomeLugar;
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