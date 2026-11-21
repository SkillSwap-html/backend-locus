package br.com.locus.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(max = 128)
    private String firebaseUid;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 160)
    private String nome;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @Column(nullable = false)
    private boolean termosAceitos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TipoUsuario tipoUsuario = TipoUsuario.VIAJANTE;

    public Usuario() {
    }

    public Long getId() {
        return id;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isTermosAceitos() {
        return termosAceitos;
    }

    public void setTermosAceitos(boolean termosAceitos) {
        this.termosAceitos = termosAceitos;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
