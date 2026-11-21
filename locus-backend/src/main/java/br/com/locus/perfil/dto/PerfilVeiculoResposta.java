package br.com.locus.perfil.dto;

import br.com.locus.perfil.PerfilVeiculo;

public class PerfilVeiculoResposta {

    private Long id;
    private String tipoVeiculo;
    private String tipoCombustivel;
    private Double consumoMedio;
    private PerfilViajanteResposta perfilViajante;

    public PerfilVeiculoResposta(PerfilVeiculo perfilVeiculo) {
        this.id = perfilVeiculo.getId();
        this.tipoVeiculo = perfilVeiculo.getTipoVeiculo();
        this.tipoCombustivel = perfilVeiculo.getTipoCombustivel();
        this.consumoMedio = perfilVeiculo.getConsumoMedio();
        this.perfilViajante =
                new PerfilViajanteResposta(perfilVeiculo.getPerfilViajante());
    }

    public Long getId() {
        return id;
    }

    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    public String getTipoCombustivel() {
        return tipoCombustivel;
    }

    public Double getConsumoMedio() {
        return consumoMedio;
    }

    public PerfilViajanteResposta getPerfilViajante() {
        return perfilViajante;
    }
}