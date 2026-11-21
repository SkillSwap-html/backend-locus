package br.com.locus.lugar.dto;

import br.com.locus.lugar.FonteLugar;
import br.com.locus.lugar.FonteLugarDetalhe;

import java.time.LocalDateTime;

public class FonteLugarDetalheResposta {

    private Long id;
    private FonteLugar fonte;
    private LocalDateTime dataConsulta;
    private String identificadorExterno;
    private LugarResposta lugar;

    public FonteLugarDetalheResposta(
            FonteLugarDetalhe fonteLugarDetalhe) {

        this.id = fonteLugarDetalhe.getId();
        this.fonte = fonteLugarDetalhe.getFonte();
        this.dataConsulta = fonteLugarDetalhe.getDataConsulta();
        this.identificadorExterno =
                fonteLugarDetalhe.getIdentificadorExterno();
        this.lugar =
                new LugarResposta(fonteLugarDetalhe.getLugar());
    }

    public Long getId() {
        return id;
    }

    public FonteLugar getFonte() {
        return fonte;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public String getIdentificadorExterno() {
        return identificadorExterno;
    }

    public LugarResposta getLugar() {
        return lugar;
    }
}