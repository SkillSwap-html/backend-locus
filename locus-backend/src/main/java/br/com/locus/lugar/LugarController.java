package br.com.locus.lugar;

import br.com.locus.lugar.dto.AtualizarLugarRequest;
import br.com.locus.lugar.dto.LugarResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lugares")
public class LugarController {

    private final LugarService lugarService;

    public LugarController(LugarService lugarService) {
        this.lugarService = lugarService;
    }

    @PostMapping("/destino/{destinoId}")
    public LugarResposta cadastrar(
            @PathVariable Long destinoId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody Lugar lugar) {

        Lugar lugarSalvo = lugarService.salvar(
                destinoId,
                usuarioSolicitanteId,
                lugar
        );

        return new LugarResposta(lugarSalvo);
    }

    @GetMapping("/destino/{destinoId}")
    public List<LugarResposta> listarPorDestino(
            @PathVariable Long destinoId,
            @RequestParam Long usuarioSolicitanteId) {

        return lugarService
                .listarPorDestino(
                        destinoId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(LugarResposta::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LugarResposta> buscarPorId(
            @PathVariable Long id,
            @RequestParam Long usuarioSolicitanteId) {

        return lugarService
                .buscarPorId(
                        id,
                        usuarioSolicitanteId
                )
                .map(LugarResposta::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public LugarResposta atualizar(
            @PathVariable Long id,
            @RequestBody AtualizarLugarRequest request,
            @RequestParam Long usuarioSolicitanteId) {

        Lugar lugarAtualizado =
                lugarService.atualizarLugar(
                        id,
                        request,
                        usuarioSolicitanteId
                );

        return new LugarResposta(lugarAtualizado);
    }
}