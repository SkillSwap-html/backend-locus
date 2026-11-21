package br.com.locus.versao;

import br.com.locus.viagem.Viagem;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "versoes_roteiro")
public class VersaoRoteiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagem viagem;

    @Column(nullable = false)
    private Integer numeroVersao;

    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    public VersaoRoteiro() {
    }

    public Long getId() {
        return id;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
    }

    public Integer getNumeroVersao() {
        return numeroVersao;
    }

    public void setNumeroVersao(Integer numeroVersao) {
        this.numeroVersao = numeroVersao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}