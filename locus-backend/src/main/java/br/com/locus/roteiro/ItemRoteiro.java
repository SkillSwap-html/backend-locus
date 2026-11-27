package br.com.locus.roteiro;

import br.com.locus.lugar.Lugar;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "itens_roteiro")
public class ItemRoteiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long versao;

    @ManyToOne
    @JoinColumn(name = "dia_roteiro_id", nullable = false)
    private DiaRoteiro diaRoteiro;

    @ManyToOne
    @JoinColumn(name = "lugar_id")
    private Lugar lugar;

    @Column(nullable = false)
    private String titulo;

    private LocalTime horarioInicio;

    private Integer duracaoMinutos;

    private Integer ordem;

    private Double custoEstimado;

    private String observacoes;

    private String motivoRecomendacao;

    private Boolean favorito = false;

    public ItemRoteiro() {

    }

    public Long getId() {
        return id;
    }

    public Long getVersao() {
        return versao;
    }

    public DiaRoteiro getDiaRoteiro() {
        return diaRoteiro;
    }

    public void setDiaRoteiro(DiaRoteiro diaRoteiro) {
        this.diaRoteiro = diaRoteiro;
    }

    public Lugar getLugar() {
        return lugar;
    }

    public void setLugar(Lugar lugar) {
        this.lugar = lugar;
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