package br.com.locus.perfil;

import br.com.locus.usuario.Usuario;
import jakarta.persistence.*;

@Entity
@Table(name = "perfis_viajante")
public class PerfilViajante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    private Integer idade;

    private String ritmoViagem;

    private String interesses;

    private String mobilidade;

    private String preferenciaHospedagem;

    public PerfilViajante() {
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getRitmoViagem() {
        return ritmoViagem;
    }

    public void setRitmoViagem(String ritmoViagem) {
        this.ritmoViagem = ritmoViagem;
    }

    public String getInteresses() {
        return interesses;
    }

    public void setInteresses(String interesses) {
        this.interesses = interesses;
    }

    public String getMobilidade() {
        return mobilidade;
    }

    public void setMobilidade(String mobilidade) {
        this.mobilidade = mobilidade;
    }

    public String getPreferenciaHospedagem() {
        return preferenciaHospedagem;
    }

    public void setPreferenciaHospedagem(String preferenciaHospedagem) {
        this.preferenciaHospedagem = preferenciaHospedagem;
    }
}