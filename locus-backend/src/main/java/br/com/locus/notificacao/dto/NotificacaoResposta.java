package br.com.locus.notificacao.dto;

import br.com.locus.notificacao.Notificacao;
import br.com.locus.notificacao.TipoNotificacao;

import java.time.LocalDateTime;

public class NotificacaoResposta {

    private Long id;
    private Long usuarioId;
    private TipoNotificacao tipo;
    private String titulo;
    private String mensagem;
    private boolean lida;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataLeitura;

    public NotificacaoResposta() {
    }

    public NotificacaoResposta(Notificacao notificacao) {
        this.id = notificacao.getId();
        this.usuarioId = notificacao.getUsuario().getId();
        this.tipo = notificacao.getTipo();
        this.titulo = notificacao.getTitulo();
        this.mensagem = notificacao.getMensagem();
        this.lida = notificacao.isLida();
        this.dataCriacao = notificacao.getDataCriacao();
        this.dataLeitura = notificacao.getDataLeitura();
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isLida() {
        return lida;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataLeitura() {
        return dataLeitura;
    }
}