package br.com.locus.perfil;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "preferencias_viagem")
public class PreferenciaViagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "perfil_viajante_id", nullable = false, unique = true)
    private PerfilViajante perfilViajante;

    private String origemPreferida;

    private String destinoPreferido;

    @Enumerated(EnumType.STRING)
    private TipoOrcamento tipoOrcamento;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorOrcamento;

    public PreferenciaViagem() {
    }

    public Long getId() {
        return id;
    }

    public PerfilViajante getPerfilViajante() {
        return perfilViajante;
    }

    public void setPerfilViajante(PerfilViajante perfilViajante) {
        this.perfilViajante = perfilViajante;
    }

    public String getOrigemPreferida() {
        return origemPreferida;
    }

    public void setOrigemPreferida(String origemPreferida) {
        this.origemPreferida = origemPreferida;
    }

    public String getDestinoPreferido() {
        return destinoPreferido;
    }

    public void setDestinoPreferido(String destinoPreferido) {
        this.destinoPreferido = destinoPreferido;
    }

    public TipoOrcamento getTipoOrcamento() {
        return tipoOrcamento;
    }

    public void setTipoOrcamento(TipoOrcamento tipoOrcamento) {
        this.tipoOrcamento = tipoOrcamento;
    }

    public BigDecimal getValorOrcamento() {
        return valorOrcamento;
    }

    public void setValorOrcamento(BigDecimal valorOrcamento) {
        this.valorOrcamento = valorOrcamento;
    }
}