package br.com.locus.compartilhamento.dto;

import br.com.locus.compartilhamento.LinkCompartilhamento;

import java.time.LocalDateTime;

public class LinkCompartilhamentoResposta {

    private Long id;
    private Long viagemId;
    private String token;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataRevogacao;

    public LinkCompartilhamentoResposta() {
    }

    public LinkCompartilhamentoResposta(LinkCompartilhamento link) {

        this.id = link.getId();
        this.viagemId = link.getViagem().getId();
        this.token = link.getToken();
        this.ativo = link.getAtivo();
        this.dataCriacao = link.getDataCriacao();
        this.dataRevogacao = link.getDataRevogacao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getViagemId() {
        return viagemId;
    }

    public void setViagemId(Long viagemId) {
        this.viagemId = viagemId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataRevogacao() {
        return dataRevogacao;
    }

    public void setDataRevogacao(LocalDateTime dataRevogacao) {
        this.dataRevogacao = dataRevogacao;
    }
}