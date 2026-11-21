package br.com.locus.versao;

public class ComparacaoVersaoRoteiro {

    private String tipo;
    private String item;
    private String detalhe;

    public ComparacaoVersaoRoteiro() {
    }

    public ComparacaoVersaoRoteiro(
            String tipo,
            String item,
            String detalhe) {

        this.tipo = tipo;
        this.item = item;
        this.detalhe = detalhe;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }
}