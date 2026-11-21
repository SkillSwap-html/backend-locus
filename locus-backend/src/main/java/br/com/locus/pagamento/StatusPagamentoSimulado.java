package br.com.locus.pagamento;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusPagamentoSimulado {

    INICIADO("iniciado"),
    CONCLUIDO("concluído"),
    INTERROMPIDO("interrompido");

    private final String descricao;

    StatusPagamentoSimulado(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }
}