package br.com.locus.perfil.dto;

import br.com.locus.perfil.PreferenciaViagem;
import br.com.locus.perfil.TipoOrcamento;

import java.math.BigDecimal;

public class PreferenciaViagemResposta {

    private Long id;
    private String origemPreferida;
    private String destinoPreferido;
    private TipoOrcamento tipoOrcamento;
    private BigDecimal valorOrcamento;
    private PerfilViajanteResposta perfilViajante;

    public PreferenciaViagemResposta(PreferenciaViagem preferenciaViagem) {
        this.id = preferenciaViagem.getId();
        this.origemPreferida = preferenciaViagem.getOrigemPreferida();
        this.destinoPreferido = preferenciaViagem.getDestinoPreferido();
        this.tipoOrcamento = preferenciaViagem.getTipoOrcamento();
        this.valorOrcamento = preferenciaViagem.getValorOrcamento();
        this.perfilViajante =
                new PerfilViajanteResposta(preferenciaViagem.getPerfilViajante());
    }

    public Long getId() {
        return id;
    }

    public String getOrigemPreferida() {
        return origemPreferida;
    }

    public String getDestinoPreferido() {
        return destinoPreferido;
    }

    public TipoOrcamento getTipoOrcamento() {
        return tipoOrcamento;
    }

    public BigDecimal getValorOrcamento() {
        return valorOrcamento;
    }

    public PerfilViajanteResposta getPerfilViajante() {
        return perfilViajante;
    }
}