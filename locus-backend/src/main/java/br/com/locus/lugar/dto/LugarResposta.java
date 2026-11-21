package br.com.locus.lugar.dto;

import br.com.locus.lugar.Lugar;
import br.com.locus.viagem.dto.DestinoResposta;

public class LugarResposta {

    private Long id;
    private String nome;
    private String categoria;
    private String endereco;
    private Double latitude;
    private Double longitude;
    private DestinoResposta destino;

    public LugarResposta(Lugar lugar) {
        this.id = lugar.getId();
        this.nome = lugar.getNome();
        this.categoria = lugar.getCategoria();
        this.endereco = lugar.getEndereco();
        this.latitude = lugar.getLatitude();
        this.longitude = lugar.getLongitude();
        this.destino = new DestinoResposta(lugar.getDestino());
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getEndereco() {
        return endereco;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public DestinoResposta getDestino() {
        return destino;
    }
}