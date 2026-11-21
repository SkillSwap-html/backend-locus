package br.com.locus.viagem.dto;

import br.com.locus.perfil.ModoTransporte;
import br.com.locus.perfil.TipoOrcamento;
import br.com.locus.usuario.dto.UsuarioResposta;
import br.com.locus.viagem.ViabilidadeTransporteTerrestre;
import br.com.locus.viagem.Viagem;

import java.time.LocalDate;

public class ViagemResposta {

    private Long id;
    private String origem;
    private String destino;
    private String titulo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private ModoTransporte modoTransporte;
    private TipoOrcamento tipoOrcamento;
    private boolean mochilao;
    private ViabilidadeTransporteTerrestre viabilidadeTransporteTerrestre;
    private UsuarioResposta usuario;

    public ViagemResposta(Viagem viagem) {
        this.id = viagem.getId();
        this.origem = viagem.getOrigem();
        this.destino = viagem.getDestino();
        this.titulo = viagem.getTitulo();
        this.dataInicio = viagem.getDataInicio();
        this.dataFim = viagem.getDataFim();
        this.modoTransporte = viagem.getModoTransporte();
        this.tipoOrcamento = viagem.getTipoOrcamento();
        this.mochilao = viagem.isMochilao();

        if (viagem.getViabilidadeTransporteTerrestre() == null) {
            this.viabilidadeTransporteTerrestre =
                    ViabilidadeTransporteTerrestre.NAO_VERIFICADA;
        } else {
            this.viabilidadeTransporteTerrestre =
                    viagem.getViabilidadeTransporteTerrestre();
        }

        if (viagem.getUsuario() != null) {
            this.usuario = new UsuarioResposta(
                    viagem.getUsuario()
            );
        }
    }

    public Long getId() {
        return id;
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public ModoTransporte getModoTransporte() {
        return modoTransporte;
    }

    public TipoOrcamento getTipoOrcamento() {
        return tipoOrcamento;
    }

    public boolean isMochilao() {
        return mochilao;
    }

    public ViabilidadeTransporteTerrestre getViabilidadeTransporteTerrestre() {
        return viabilidadeTransporteTerrestre;
    }

    public UsuarioResposta getUsuario() {
        return usuario;
    }
}
