package br.com.locus.perfil;

import jakarta.persistence.*;

@Entity
@Table(name = "preferencias_transporte")
public class PreferenciaTransporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "perfil_viajante_id", nullable = false, unique = true)
    private PerfilViajante perfilViajante;

    @Enumerated(EnumType.STRING)
    private ModoTransporte modoTransporte;

    @Enumerated(EnumType.STRING)
    private PrioridadePreferencia prioridade;

    public PreferenciaTransporte() {
    }

    public Long getId() {
        return id;
    }

    public PerfilViajante getPerfilViajante() {
        return perfilViajante;
    }

    public void setPerfilViajante(PerfilViajante perfilViajante) {
        this.perfilViajante = perfilViajante;
    }

    public ModoTransporte getModoTransporte() {
        return modoTransporte;
    }

    public void setModoTransporte(ModoTransporte modoTransporte) {
        this.modoTransporte = modoTransporte;
    }

    public PrioridadePreferencia getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadePreferencia prioridade) {
        this.prioridade = prioridade;
    }
}