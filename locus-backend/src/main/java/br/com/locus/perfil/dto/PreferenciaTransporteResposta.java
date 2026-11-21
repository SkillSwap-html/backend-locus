package br.com.locus.perfil.dto;

import br.com.locus.perfil.ModoTransporte;
import br.com.locus.perfil.PreferenciaTransporte;
import br.com.locus.perfil.PrioridadePreferencia;

public class PreferenciaTransporteResposta {

    private Long id;
    private ModoTransporte modoTransporte;
    private PrioridadePreferencia prioridade;
    private PerfilViajanteResposta perfilViajante;

    public PreferenciaTransporteResposta(PreferenciaTransporte preferenciaTransporte) {
        this.id = preferenciaTransporte.getId();
        this.modoTransporte = preferenciaTransporte.getModoTransporte();
        this.prioridade = preferenciaTransporte.getPrioridade();
        this.perfilViajante =
                new PerfilViajanteResposta(preferenciaTransporte.getPerfilViajante());
    }

    public Long getId() {
        return id;
    }

    public ModoTransporte getModoTransporte() {
        return modoTransporte;
    }

    public PrioridadePreferencia getPrioridade() {
        return prioridade;
    }

    public PerfilViajanteResposta getPerfilViajante() {
        return perfilViajante;
    }
}