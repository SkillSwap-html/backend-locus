package br.com.locus.auditoria;

import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;
import java.util.UUID;

@Service
public class EventoAuditoriaService {

    private final EventoAuditoriaRepository eventoAuditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public EventoAuditoriaService(
            EventoAuditoriaRepository eventoAuditoriaRepository,
            UsuarioRepository usuarioRepository) {

        this.eventoAuditoriaRepository =
                eventoAuditoriaRepository;

        this.usuarioRepository =
                usuarioRepository;
    }

    @Transactional
    public EventoAuditoria registrar(
            Long usuarioId,
            String acao,
            String tipoRecurso,
            Long recursoId,
            ResultadoAuditoria resultado,
            String detalhes) {

        Usuario usuario = null;

        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Usuário não encontrado."
                            ));
        }

        EventoAuditoria evento =
                new EventoAuditoria();

        evento.setUsuario(usuario);
        evento.setAcao(acao);
        evento.setTipoRecurso(tipoRecurso);
        evento.setRecursoId(recursoId);
        evento.setResultado(resultado);
        evento.setDetalhes(detalhes);

        evento.setCorrelationId(
                UUID.randomUUID().toString()
        );

        return eventoAuditoriaRepository.save(evento);
    }

    public List<EventoAuditoria> listarPorUsuario(
            Long usuarioId) {

        return eventoAuditoriaRepository
                .findByUsuarioIdOrderByDataEventoDesc(
                        usuarioId
                );
    }

    public List<EventoAuditoria> listarPorRecurso(
            String tipoRecurso,
            Long recursoId) {

        return eventoAuditoriaRepository
                .findByTipoRecursoAndRecursoIdOrderByDataEventoDesc(
                        tipoRecurso,
                        recursoId
                );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EventoAuditoria registrarFalha(
            Long usuarioId,
            String acao,
            String tipoRecurso,
            Long recursoId,
            String detalhes) {

        Usuario usuario = null;

        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId)
                    .orElse(null);
        }

        EventoAuditoria evento = new EventoAuditoria();

        evento.setUsuario(usuario);
        evento.setAcao(acao);
        evento.setTipoRecurso(tipoRecurso);
        evento.setRecursoId(recursoId);
        evento.setResultado(ResultadoAuditoria.FALHA);
        evento.setDetalhes(detalhes);
        evento.setCorrelationId(UUID.randomUUID().toString());

        return eventoAuditoriaRepository.save(evento);
    }
}