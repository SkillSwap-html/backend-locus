package br.com.locus.orcamento.dto;

import br.com.locus.orcamento.CategoriaOrcamento;
import br.com.locus.orcamento.LimiteOrcamento;
import br.com.locus.viagem.dto.ViagemResposta;

import java.math.BigDecimal;

public class LimiteOrcamentoResposta {

    private Long id;
    private CategoriaOrcamento categoria;
    private BigDecimal valorLimite;
    private ViagemResposta viagem;

    public LimiteOrcamentoResposta(LimiteOrcamento limiteOrcamento) {
        this.id = limiteOrcamento.getId();
        this.categoria = limiteOrcamento.getCategoria();
        this.valorLimite = limiteOrcamento.getValorLimite();
        this.viagem = new ViagemResposta(
                limiteOrcamento.getViagem()
        );
    }

    public Long getId() {
        return id;
    }

    public CategoriaOrcamento getCategoria() {
        return categoria;
    }

    public BigDecimal getValorLimite() {
        return valorLimite;
    }

    public ViagemResposta getViagem() {
        return viagem;
    }
}