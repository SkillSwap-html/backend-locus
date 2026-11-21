package br.com.locus.perfil;

import br.com.locus.perfil.dto.PerfilViajanteResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfis")
public class PerfilViajanteController {

    private final PerfilViajanteService perfilViajanteService;

    public PerfilViajanteController(
            PerfilViajanteService perfilViajanteService) {

        this.perfilViajanteService = perfilViajanteService;
    }

    @PostMapping("/usuario/{usuarioId}")
    public PerfilViajanteResposta cadastrar(
            @PathVariable Long usuarioId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody PerfilViajante perfilViajante) {

        PerfilViajante perfilSalvo =
                perfilViajanteService.salvar(
                        usuarioId,
                        usuarioSolicitanteId,
                        perfilViajante
                );

        return new PerfilViajanteResposta(perfilSalvo);
    }

    @GetMapping
    public List<PerfilViajanteResposta> listarTodos(
            @RequestParam Long usuarioSolicitanteId) {

        return perfilViajanteService
                .listarTodos(usuarioSolicitanteId)
                .stream()
                .map(PerfilViajanteResposta::new)
                .toList();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<PerfilViajanteResposta> buscarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam Long usuarioSolicitanteId) {

        return perfilViajanteService
                .buscarPorUsuario(
                        usuarioId,
                        usuarioSolicitanteId
                )
                .map(perfil -> ResponseEntity.ok(
                        new PerfilViajanteResposta(perfil)))
                .orElse(ResponseEntity.notFound().build());
    }
}