package br.com.locus.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoAuditoriaRepository
        extends JpaRepository<EventoAuditoria, Long> {

    List<EventoAuditoria>
    findByUsuarioIdOrderByDataEventoDesc(Long usuarioId);

    List<EventoAuditoria>
    findByTipoRecursoAndRecursoIdOrderByDataEventoDesc(
            String tipoRecurso,
            Long recursoId
    );
}