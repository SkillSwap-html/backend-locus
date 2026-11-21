package br.com.locus.perfil.dto;

import br.com.locus.perfil.PerfilViajante;
import br.com.locus.usuario.dto.UsuarioResposta;

public class PerfilViajanteResposta {

    private Long id;
    private Integer idade;
    private String ritmoViagem;
    private String interesses;
    private String mobilidade;
    private String preferenciaHospedagem;
    private UsuarioResposta usuario;

    public PerfilViajanteResposta(PerfilViajante perfilViajante) {
        this.id = perfilViajante.getId();
        this.idade = perfilViajante.getIdade();
        this.ritmoViagem = perfilViajante.getRitmoViagem();
        this.interesses = perfilViajante.getInteresses();
        this.mobilidade = perfilViajante.getMobilidade();
        this.preferenciaHospedagem = perfilViajante.getPreferenciaHospedagem();
        this.usuario = new UsuarioResposta(perfilViajante.getUsuario());
    }

    public Long getId() {
        return id;
    }

    public Integer getIdade() {
        return idade;
    }

    public String getRitmoViagem() {
        return ritmoViagem;
    }

    public String getInteresses() {
        return interesses;
    }

    public String getMobilidade() {
        return mobilidade;
    }

    public String getPreferenciaHospedagem() {
        return preferenciaHospedagem;
    }

    public UsuarioResposta getUsuario() {
        return usuario;
    }
}