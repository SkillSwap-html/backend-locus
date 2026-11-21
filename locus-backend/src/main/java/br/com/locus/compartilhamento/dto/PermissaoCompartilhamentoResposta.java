package br.com.locus.compartilhamento.dto;

import br.com.locus.compartilhamento.PermissaoCompartilhamento;
import br.com.locus.compartilhamento.TipoPermissao;

import java.time.LocalDateTime;

public class PermissaoCompartilhamentoResposta {

    private Long id;
    private Long viagemId;
    private Long usuarioId;
    private String nomeUsuario;
    private TipoPermissao tipoPermissao;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataRevogacao;

    public PermissaoCompartilhamentoResposta() {
    }

    public PermissaoCompartilhamentoResposta(
            PermissaoCompartilhamento permissao) {

        this.id = permissao.getId();
        this.tipoPermissao = permissao.getTipoPermissao();
        this.ativo = permissao.getAtivo();
        this.dataCriacao = permissao.getDataCriacao();
        this.dataRevogacao = permissao.getDataRevogacao();

        if (permissao.getViagem() != null) {
            this.viagemId = permissao.getViagem().getId();
        }

        if (permissao.getUsuario() != null) {
            this.usuarioId = permissao.getUsuario().getId();
            this.nomeUsuario = permissao.getUsuario().getNome();
        }
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public TipoPermissao getTipoPermissao() {
        return tipoPermissao;
    }

    public void setTipoPermissao(TipoPermissao tipoPermissao) {
        this.tipoPermissao = tipoPermissao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataRevogacao() {
        return dataRevogacao;
    }

    public void setDataRevogacao(LocalDateTime dataRevogacao) {
        this.dataRevogacao = dataRevogacao;
    }

}