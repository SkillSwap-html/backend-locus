package br.com.locus.auditoria.dto;

import br.com.locus.auditoria.EventoAuditoria;
import br.com.locus.auditoria.ResultadoAuditoria;

import java.time.LocalDateTime;

public class EventoAuditoriaResposta {

    private Long id;
    private String acao;
    private String tipoRecurso;
    private Long recursoId;
    private ResultadoAuditoria resultado;
    private String detalhes;
    private String correlationId;
    private LocalDateTime dataEvento;
    private Long usuarioId;
    private String nomeUsuario;

    public EventoAuditoriaResposta(EventoAuditoria evento) {

        this.id = evento.getId();
        this.acao = evento.getAcao();
        this.tipoRecurso = evento.getTipoRecurso();
        this.recursoId = evento.getRecursoId();
        this.resultado = evento.getResultado();
        this.detalhes = evento.getDetalhes();
        this.correlationId = evento.getCorrelationId();
        this.dataEvento = evento.getDataEvento();

        if (evento.getUsuario() != null) {
            this.usuarioId = evento.getUsuario().getId();
            this.nomeUsuario = evento.getUsuario().getNome();
        }
    }

    public Long getId() {
        return id;
    }

    public String getAcao() {
        return acao;
    }

    public String getTipoRecurso() {
        return tipoRecurso;
    }

    public Long getRecursoId() {
        return recursoId;
    }

    public ResultadoAuditoria getResultado() {
        return resultado;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public LocalDateTime getDataEvento() {
        return dataEvento;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }
}