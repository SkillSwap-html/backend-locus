package br.com.locus.viagem;

import br.com.locus.viagem.dto.DestinoResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinos")
public class DestinoController {

    private final DestinoService destinoService;

    public DestinoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    @PostMapping("/viagem/{viagemId}")
    public DestinoResposta cadastrar(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody Destino destino) {

        Destino destinoSalvo =
                destinoService.salvar(
                        viagemId,
                        usuarioSolicitanteId,
                        destino
                );

        return new DestinoResposta(destinoSalvo);
    }

    @GetMapping("/viagem/{viagemId}")
    public List<DestinoResposta> listarPorViagem(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        return destinoService
                .listarPorViagem(
                        viagemId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(DestinoResposta::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinoResposta> buscarPorId(
            @PathVariable Long id,
            @RequestParam Long usuarioSolicitanteId) {

        return destinoService
                .buscarPorId(
                        id,
                        usuarioSolicitanteId
                )
                .map(DestinoResposta::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}