package br.com.locus.viagem.dto;

import br.com.locus.viagem.Destino;

public class DestinoResposta {

    private Long id;
    private String cidade;
    private String estado;
    private String pais;
    private ViagemResposta viagem;

    public DestinoResposta(Destino destino) {
        this.id = destino.getId();
        this.cidade = destino.getCidade();
        this.estado = destino.getEstado();
        this.pais = destino.getPais();
        this.viagem = new ViagemResposta(destino.getViagem());
    }

    public Long getId() {
        return id;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getPais() {
        return pais;
    }

    public ViagemResposta getViagem() {
        return viagem;
    }
}