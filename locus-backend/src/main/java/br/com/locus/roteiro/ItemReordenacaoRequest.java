package br.com.locus.roteiro;

public class ItemReordenacaoRequest {

    private Long itemId;
    private Long versaoEsperada;

    public ItemReordenacaoRequest() {
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getVersaoEsperada() {
        return versaoEsperada;
    }

    public void setVersaoEsperada(Long versaoEsperada) {
        this.versaoEsperada = versaoEsperada;
    }
}