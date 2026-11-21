package br.com.locus.preco.dto;

import br.com.locus.preco.SnapshotPreco;
import br.com.locus.preco.TipoFontePreco;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SnapshotPrecoResposta {

    private Long id;
    private String tipoOferta;
    private Long ofertaId;
    private BigDecimal preco;
    private String moeda;
    private TipoFontePreco fonte;
    private LocalDateTime dataConsulta;

    public SnapshotPrecoResposta(SnapshotPreco snapshot) {
        this.id = snapshot.getId();
        this.tipoOferta = snapshot.getTipoOferta();
        this.ofertaId = snapshot.getOfertaId();
        this.preco = snapshot.getPreco();
        this.moeda = snapshot.getMoeda();
        this.fonte = snapshot.getFonte();
        this.dataConsulta = snapshot.getDataConsulta();
    }

    public Long getId() {
        return id;
    }

    public String getTipoOferta() {
        return tipoOferta;
    }

    public Long getOfertaId() {
        return ofertaId;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public String getMoeda() {
        return moeda;
    }

    public TipoFontePreco getFonte() {
        return fonte;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }
}