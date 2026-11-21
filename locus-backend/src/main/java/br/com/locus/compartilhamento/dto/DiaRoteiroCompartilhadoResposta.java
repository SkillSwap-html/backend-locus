package br.com.locus.compartilhamento.dto;

import br.com.locus.roteiro.DiaRoteiro;

import java.time.LocalDate;
import java.util.List;

public class DiaRoteiroCompartilhadoResposta {

    private Long id;
    private LocalDate data;
    private String observacoes;
    private List<ItemRoteiroCompartilhadoResposta> itens;

    public DiaRoteiroCompartilhadoResposta() {
    }

    public DiaRoteiroCompartilhadoResposta(
            DiaRoteiro dia,
            List<ItemRoteiroCompartilhadoResposta> itens) {

        this.id = dia.getId();
        this.data = dia.getData();
        this.observacoes = dia.getObservacoes();
        this.itens = itens;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public List<ItemRoteiroCompartilhadoResposta> getItens() {
        return itens;
    }

    public void setItens(
            List<ItemRoteiroCompartilhadoResposta> itens) {
        this.itens = itens;
    }
}