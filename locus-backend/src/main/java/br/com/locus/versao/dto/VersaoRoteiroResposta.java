package br.com.locus.versao.dto;

import br.com.locus.versao.VersaoRoteiro;

import java.time.LocalDateTime;

public class VersaoRoteiroResposta {

    private Long id;
    private Long viagemId;
    private Integer numeroVersao;
    private String descricao;
    private LocalDateTime dataCriacao;

    public VersaoRoteiroResposta(VersaoRoteiro versao) {
        this.id = versao.getId();
        this.viagemId = versao.getViagem().getId();
        this.numeroVersao = versao.getNumeroVersao();
        this.descricao = versao.getDescricao();
        this.dataCriacao = versao.getDataCriacao();
    }

    public Long getId() {
        return id;
    }

    public Long getViagemId() {
        return viagemId;
    }

    public Integer getNumeroVersao() {
        return numeroVersao;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}