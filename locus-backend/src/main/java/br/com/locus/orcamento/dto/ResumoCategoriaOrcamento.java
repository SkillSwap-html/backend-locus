package br.com.locus.orcamento.dto;

import br.com.locus.orcamento.CategoriaOrcamento;

import java.math.BigDecimal;

public class ResumoCategoriaOrcamento {

    private CategoriaOrcamento categoria;
    private BigDecimal subtotal;
    private BigDecimal limite;
    private BigDecimal saldo;
    private BigDecimal excesso;

    public ResumoCategoriaOrcamento() {
    }

    public ResumoCategoriaOrcamento(
            CategoriaOrcamento categoria,
            BigDecimal subtotal,
            BigDecimal limite,
            BigDecimal saldo,
            BigDecimal excesso) {

        this.categoria = categoria;
        this.subtotal = subtotal;
        this.limite = limite;
        this.saldo = saldo;
        this.excesso = excesso;
    }

    public CategoriaOrcamento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaOrcamento categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getExcesso() {
        return excesso;
    }

    public void setExcesso(BigDecimal excesso) {
        this.excesso = excesso;
    }
}