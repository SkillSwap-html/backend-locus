package br.com.locus.orcamento;

import br.com.locus.viagem.Viagem;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "limites_orcamento")
public class LimiteOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagem viagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaOrcamento categoria;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorLimite;

    public LimiteOrcamento() {
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

    public CategoriaOrcamento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaOrcamento categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValorLimite() {
        return valorLimite;
    }

    public void setValorLimite(BigDecimal valorLimite) {
        this.valorLimite = valorLimite;
    }
}