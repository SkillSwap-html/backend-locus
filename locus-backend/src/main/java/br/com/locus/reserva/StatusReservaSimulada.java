package br.com.locus.reserva;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusReservaSimulada {

    RASCUNHO("rascunho"),
    AGUARDANDO_PAGAMENTO_SIMULADO("aguardando pagamento simulado"),
    PAGAMENTO_SIMULADO_CONCLUIDO("pagamento simulado concluído"),
    CONFIRMADA_EM_SIMULACAO("confirmada em simulação"),
    CANCELADA_EM_SIMULACAO("cancelada em simulação");

    private final String descricao;

    StatusReservaSimulada(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }
}