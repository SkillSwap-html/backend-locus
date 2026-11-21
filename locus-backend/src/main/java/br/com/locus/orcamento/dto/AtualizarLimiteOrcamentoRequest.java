package br.com.locus.orcamento.dto;

import java.math.BigDecimal;

public class AtualizarLimiteOrcamentoRequest {

    private BigDecimal valorLimite;
    private boolean confirmacaoUsuario;

    public AtualizarLimiteOrcamentoRequest() {
    }

    public BigDecimal getValorLimite() {
        return valorLimite;
    }

    public void setValorLimite(BigDecimal valorLimite) {
        this.valorLimite = valorLimite;
    }

    public boolean isConfirmacaoUsuario() {
        return confirmacaoUsuario;
    }

    public void setConfirmacaoUsuario(boolean confirmacaoUsuario) {
        this.confirmacaoUsuario = confirmacaoUsuario;
    }
}