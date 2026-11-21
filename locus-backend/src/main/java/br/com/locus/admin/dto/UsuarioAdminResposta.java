package br.com.locus.admin.dto;

import br.com.locus.usuario.TipoUsuario;
import br.com.locus.usuario.Usuario;

public class UsuarioAdminResposta {

    private Long id;
    private String nome;
    private String email;
    private TipoUsuario tipoUsuario;
    private boolean termosAceitos;

    public UsuarioAdminResposta(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.tipoUsuario = usuario.getTipoUsuario();
        this.termosAceitos = usuario.isTermosAceitos();
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

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public boolean isTermosAceitos() {
        return termosAceitos;
    }
}