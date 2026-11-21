package br.com.locus.usuario.dto;

import br.com.locus.usuario.TipoUsuario;
import br.com.locus.usuario.Usuario;

public class UsuarioResposta {

    private Long id;
    private String nome;
    private String email;
    private boolean termosAceitos;
    private TipoUsuario tipoUsuario;

    public UsuarioResposta(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.termosAceitos = usuario.isTermosAceitos();
        this.tipoUsuario = usuario.getTipoUsuario();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public boolean isTermosAceitos() {
        return termosAceitos;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }
}