package br.com.locus.perfil;

import jakarta.persistence.*;

@Entity
@Table(name = "perfis_veiculo")
public class PerfilVeiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "perfil_viajante_id", nullable = false, unique = true)
    private PerfilViajante perfilViajante;

    private String tipoVeiculo;

    private String tipoCombustivel;

    private Double consumoMedio;

    public PerfilVeiculo() {
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

    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(String tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getTipoCombustivel() {
        return tipoCombustivel;
    }

    public void setTipoCombustivel(String tipoCombustivel) {
        this.tipoCombustivel = tipoCombustivel;
    }

    public Double getConsumoMedio() {
        return consumoMedio;
    }

    public void setConsumoMedio(Double consumoMedio) {
        this.consumoMedio = consumoMedio;
    }
}
