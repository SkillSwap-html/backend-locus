package br.com.locus.orcamento.dto;

import br.com.locus.orcamento.CategoriaOrcamento;
import br.com.locus.preco.ClassificacaoPreco;

import java.math.BigDecimal;

public class ItemOrcamentoComPrecoResposta {

    private Long itemId;
    private CategoriaOrcamento categoria;
    private String descricao;
    private BigDecimal valor;
    private String tipoOferta;
    private Long ofertaId;
    private ClassificacaoPreco classificacaoPreco;
    private BigDecimal mediaHistorica;
    private String justificativa;

    public ItemOrcamentoComPrecoResposta(
            Long itemId,
            CategoriaOrcamento categoria,
            String descricao,
            BigDecimal valor,
            String tipoOferta,
            Long ofertaId,
            ClassificacaoPreco classificacaoPreco,
            BigDecimal mediaHistorica,
            String justificativa) {

        this.itemId = itemId;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.tipoOferta = tipoOferta;
        this.ofertaId = ofertaId;
        this.classificacaoPreco = classificacaoPreco;
        this.mediaHistorica = mediaHistorica;
        this.justificativa = justificativa;
    }

    public Long getItemId() {
        return itemId;
    }

    public CategoriaOrcamento getCategoria() {
        return categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getTipoOferta() {
        return tipoOferta;
    }

    public Long getOfertaId() {
        return ofertaId;
    }

    public ClassificacaoPreco getClassificacaoPreco() {
        return classificacaoPreco;
    }

    public BigDecimal getMediaHistorica() {
        return mediaHistorica;
    }

    public String getJustificativa() {
        return justificativa;
    }
}