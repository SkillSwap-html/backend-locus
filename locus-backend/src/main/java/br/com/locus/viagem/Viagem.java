package br.com.locus.viagem;

import br.com.locus.perfil.ModoTransporte;
import br.com.locus.perfil.TipoOrcamento;
import br.com.locus.usuario.Usuario;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "viagens")
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String origem;

    @Column(nullable = false)
    private String destino;

    @Column(nullable = false)
    private String titulo;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    private ModoTransporte modoTransporte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOrcamento tipoOrcamento = TipoOrcamento.ABERTO;

    @Column(nullable = false)
    private boolean mochilao = false;

    @Enumerated(EnumType.STRING)
    private ViabilidadeTransporteTerrestre viabilidadeTransporteTerrestre;

    public ViabilidadeTransporteTerrestre getViabilidadeTransporteTerrestre() {
        return viabilidadeTransporteTerrestre;
    }

    public void setViabilidadeTransporteTerrestre(
            ViabilidadeTransporteTerrestre viabilidadeTransporteTerrestre) {

        this.viabilidadeTransporteTerrestre =
                viabilidadeTransporteTerrestre;
    }

    public Viagem() {
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getOrigem() {
        return origem;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public ModoTransporte getModoTransporte() {
        return modoTransporte;
    }

    public void setModoTransporte(ModoTransporte modoTransporte) {
        this.modoTransporte = modoTransporte;
    }

    public TipoOrcamento getTipoOrcamento() {
        return tipoOrcamento;
    }

    public void setTipoOrcamento(TipoOrcamento tipoOrcamento) {
        this.tipoOrcamento = tipoOrcamento;
    }

    public boolean isMochilao() {
        return mochilao;
    }

    public void setMochilao(boolean mochilao) {
        this.mochilao = mochilao;
    }
}
