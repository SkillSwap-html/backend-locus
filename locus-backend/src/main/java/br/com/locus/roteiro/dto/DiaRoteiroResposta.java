package br.com.locus.roteiro.dto;

import br.com.locus.roteiro.DiaRoteiro;
import br.com.locus.viagem.dto.ViagemResposta;

import java.time.LocalDate;

public class DiaRoteiroResposta {

    private Long id;
    private LocalDate data;
    private String observacoes;
    private ViagemResposta viagem;

    public DiaRoteiroResposta(DiaRoteiro diaRoteiro) {
        this.id = diaRoteiro.getId();
        this.data = diaRoteiro.getData();
        this.observacoes = diaRoteiro.getObservacoes();

        if (diaRoteiro.getViagem() != null) {
            this.viagem = new ViagemResposta(
                    diaRoteiro.getViagem()
            );
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public ViagemResposta getViagem() {
        return viagem;
    }
}