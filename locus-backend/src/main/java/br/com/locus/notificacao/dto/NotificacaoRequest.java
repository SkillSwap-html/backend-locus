package br.com.locus.notificacao.dto;

import br.com.locus.notificacao.TipoNotificacao;

public class NotificacaoRequest {

    private TipoNotificacao tipo;
    private String titulo;
    private String mensagem;

    public NotificacaoRequest() {
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacao tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}