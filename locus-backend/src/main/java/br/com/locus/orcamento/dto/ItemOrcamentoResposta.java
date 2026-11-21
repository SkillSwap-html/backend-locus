package br.com.locus.orcamento.dto;

import br.com.locus.orcamento.CategoriaOrcamento;
import br.com.locus.orcamento.FonteItemOrcamento;
import br.com.locus.orcamento.ItemOrcamento;
import br.com.locus.viagem.dto.ViagemResposta;

import java.math.BigDecimal;

public class ItemOrcamentoResposta {

    private Long id;
    private CategoriaOrcamento categoria;
    private String descricao;
    private BigDecimal valor;
    private FonteItemOrcamento fonte;
    private String tipoOferta;
    private Long ofertaId;
    private ViagemResposta viagem;

    public ItemOrcamentoResposta(ItemOrcamento item) {
        this.id = item.getId();
        this.categoria = item.getCategoria();
        this.descricao = item.getDescricao();
        this.valor = item.getValor();
        this.fonte = item.getFonte();
        this.tipoOferta = item.getTipoOferta();
        this.ofertaId = item.getOfertaId();

        if (item.getViagem() != null) {
            this.viagem = new ViagemResposta(item.getViagem());
        }
    }

    public Long getId() {
        return id;
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

    public FonteItemOrcamento getFonte() {
        return fonte;
    }

    public String getTipoOferta() {
        return tipoOferta;
    }

    public Long getOfertaId() {
        return ofertaId;
    }

    public ViagemResposta getViagem() {
        return viagem;
    }
}