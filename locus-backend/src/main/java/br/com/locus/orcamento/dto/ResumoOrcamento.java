package br.com.locus.orcamento.dto;

import br.com.locus.perfil.TipoOrcamento;

import java.math.BigDecimal;
import java.util.List;

public class ResumoOrcamento {

    private TipoOrcamento tipoOrcamento;

    private BigDecimal limiteTotal;
    private BigDecimal totalGasto;
    private BigDecimal saldoTotal;
    private BigDecimal excessoTotal;

    private List<ResumoCategoriaOrcamento> categorias;

    public ResumoOrcamento() {
    }

    public TipoOrcamento getTipoOrcamento() {
        return tipoOrcamento;
    }

    public void setTipoOrcamento(TipoOrcamento tipoOrcamento) {
        this.tipoOrcamento = tipoOrcamento;
    }

    public BigDecimal getLimiteTotal() {
        return limiteTotal;
    }

    public void setLimiteTotal(BigDecimal limiteTotal) {
        this.limiteTotal = limiteTotal;
    }

    public BigDecimal getTotalGasto() {
        return totalGasto;
    }

    public void setTotalGasto(BigDecimal totalGasto) {
        this.totalGasto = totalGasto;
    }

    public BigDecimal getSaldoTotal() {
        return saldoTotal;
    }

    public void setSaldoTotal(BigDecimal saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    public BigDecimal getExcessoTotal() {
        return excessoTotal;
    }

    public void setExcessoTotal(BigDecimal excessoTotal) {
        this.excessoTotal = excessoTotal;
    }

    public List<ResumoCategoriaOrcamento> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<ResumoCategoriaOrcamento> categorias) {
        this.categorias = categorias;
    }
}