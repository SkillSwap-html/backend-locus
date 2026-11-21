package br.com.locus.viagem;

import br.com.locus.viagem.dto.ViagemResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/viagens")
public class ViagemController {

    private final ViagemService viagemService;

    public ViagemController(ViagemService viagemService) {
        this.viagemService = viagemService;
    }

    @PostMapping("/usuario/{usuarioId}")
    public ViagemResposta cadastrar(
            @PathVariable Long usuarioId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody Viagem viagem) {

        Viagem viagemSalva =
                viagemService.salvar(
                        usuarioId,
                        usuarioSolicitanteId,
                        viagem
                );

        return new ViagemResposta(viagemSalva);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ViagemResposta> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam Long usuarioSolicitanteId) {

        return viagemService
                .listarPorUsuario(
                        usuarioId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(ViagemResposta::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViagemResposta> buscarPorId(
            @PathVariable Long id,
            @RequestParam Long usuarioSolicitanteId) {

        return viagemService
                .buscarPorId(id, usuarioSolicitanteId)
                .map(viagem -> ResponseEntity.ok(
                        new ViagemResposta(viagem)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ViagemResposta atualizar(
            @PathVariable Long id,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody Viagem viagem) {

        return new ViagemResposta(
                viagemService.atualizar(id, usuarioSolicitanteId, viagem)
        );
    }
}
