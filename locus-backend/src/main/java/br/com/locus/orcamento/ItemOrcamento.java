package br.com.locus.orcamento;

import br.com.locus.transporte.EstimativaRota;
import br.com.locus.viagem.Viagem;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_orcamento")
public class ItemOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagem viagem;

    @ManyToOne
    @JoinColumn(name = "estimativa_rota_id")
    private EstimativaRota estimativaRota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaOrcamento categoria;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private FonteItemOrcamento fonte;

    private String tipoOferta;

    private Long ofertaId;

    public ItemOrcamento() {
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

    public EstimativaRota getEstimativaRota() {
        return estimativaRota;
    }

    public void setEstimativaRota(EstimativaRota estimativaRota) {
        this.estimativaRota = estimativaRota;
    }

    public FonteItemOrcamento getFonte() {
        return fonte;
    }

    public void setFonte(FonteItemOrcamento fonte) {
        this.fonte = fonte;
    }

    public CategoriaOrcamento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaOrcamento categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
}