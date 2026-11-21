package br.com.locus.preco;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "snapshots_preco")
public class SnapshotPreco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoOferta;

    @Column(nullable = false)
    private Long ofertaId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private String moeda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoFontePreco fonte;

    @Column(nullable = false)
    private LocalDateTime dataConsulta;

    public SnapshotPreco() {
    }

    public Long getId() {
        return id;
    }

    public String getTipoOferta() {
        return tipoOferta;
    }

    public void setTipoOferta(String tipoOferta) {
        this.tipoOferta = tipoOferta;
    }

    public Long getOfertaId() {
        return ofertaId;
    }

    public void setOfertaId(Long ofertaId) {
        this.ofertaId = ofertaId;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public TipoFontePreco getFonte() {
        return fonte;
    }

    public void setFonte(TipoFontePreco fonte) {
        this.fonte = fonte;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDateTime dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    @PrePersist
    public void preencherDataConsulta() {

        if (dataConsulta == null) {
            dataConsulta = LocalDateTime.now();
        }
    }
}