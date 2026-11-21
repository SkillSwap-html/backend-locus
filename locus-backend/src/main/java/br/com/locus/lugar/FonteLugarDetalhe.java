package br.com.locus.lugar;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fontes_lugar")
public class FonteLugarDetalhe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "lugar_id", nullable = false, unique = true)
    private Lugar lugar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FonteLugar fonte;

    private LocalDateTime dataConsulta;

    private String identificadorExterno;

    public FonteLugarDetalhe() {
    }

    public Long getId() {
        return id;
    }

    public Lugar getLugar() {
        return lugar;
    }

    public void setLugar(Lugar lugar) {
        this.lugar = lugar;
    }

    public FonteLugar getFonte() {
        return fonte;
    }

    public void setFonte(FonteLugar fonte) {
        this.fonte = fonte;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDateTime dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public String getIdentificadorExterno() {
        return identificadorExterno;
    }

    public void setIdentificadorExterno(String identificadorExterno) {
        this.identificadorExterno = identificadorExterno;
    }
}