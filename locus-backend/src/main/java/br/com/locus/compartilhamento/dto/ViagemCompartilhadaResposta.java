package br.com.locus.compartilhamento.dto;

import br.com.locus.perfil.ModoTransporte;
import br.com.locus.viagem.Viagem;

import java.time.LocalDate;

public class ViagemCompartilhadaResposta {

    private Long viagemId;
    private String origem;
    private String destino;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private ModoTransporte modoTransporte;

    public ViagemCompartilhadaResposta() {
    }

    public ViagemCompartilhadaResposta(Viagem viagem) {
        this.viagemId = viagem.getId();
        this.origem = viagem.getOrigem();
        this.destino = viagem.getDestino();
        this.dataInicio = viagem.getDataInicio();
        this.dataFim = viagem.getDataFim();
        this.modoTransporte = viagem.getModoTransporte();
    }

    public Long getViagemId() {
        return viagemId;
    }

    public void setViagemId(Long viagemId) {
        this.viagemId = viagemId;
    }

    public String getOrigem() {
        return origem;
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
}