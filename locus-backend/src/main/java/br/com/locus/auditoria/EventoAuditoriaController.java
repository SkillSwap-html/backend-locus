package br.com.locus.auditoria;

import br.com.locus.admin.ControleAcessoService;
import br.com.locus.auditoria.dto.EventoAuditoriaResposta;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class EventoAuditoriaController {

    private final EventoAuditoriaService eventoAuditoriaService;
    private final ControleAcessoService controleAcessoService;

    public EventoAuditoriaController(
            EventoAuditoriaService eventoAuditoriaService,
            ControleAcessoService controleAcessoService) {

        this.eventoAuditoriaService = eventoAuditoriaService;
        this.controleAcessoService = controleAcessoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<EventoAuditoriaResposta> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam Long usuarioSolicitanteId) {

        controleAcessoService
                .exigirResponsavelTecnicoOuAdministrador(
                        usuarioSolicitanteId
                );

        return eventoAuditoriaService
                .listarPorUsuario(usuarioId)
                .stream()
                .map(EventoAuditoriaResposta::new)
                .toList();
    }

    @GetMapping("/recurso/{tipoRecurso}/{recursoId}")
    public List<EventoAuditoriaResposta> listarPorRecurso(
            @PathVariable String tipoRecurso,
            @PathVariable Long recursoId,
            @RequestParam Long usuarioSolicitanteId) {

        controleAcessoService
                .exigirResponsavelTecnicoOuAdministrador(
                        usuarioSolicitanteId
                );

        return eventoAuditoriaService
                .listarPorRecurso(tipoRecurso, recursoId)
                .stream()
                .map(EventoAuditoriaResposta::new)
                .toList();
    }
}