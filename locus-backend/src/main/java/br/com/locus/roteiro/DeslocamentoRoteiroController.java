package br.com.locus.roteiro;

import br.com.locus.roteiro.dto.DeslocamentoRoteiroResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deslocamentos-roteiro")
public class DeslocamentoRoteiroController {

    private final DeslocamentoRoteiroService deslocamentoService;

    public DeslocamentoRoteiroController(
            DeslocamentoRoteiroService deslocamentoService) {

        this.deslocamentoService = deslocamentoService;
    }

    @PostMapping("/origem/{itemOrigemId}/destino/{itemDestinoId}")
    public DeslocamentoRoteiroResposta cadastrar(
            @PathVariable Long itemOrigemId,
            @PathVariable Long itemDestinoId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody DeslocamentoRoteiro deslocamento) {

        DeslocamentoRoteiro deslocamentoSalvo =
                deslocamentoService.salvar(
                        itemOrigemId,
                        itemDestinoId,
                        usuarioSolicitanteId,
                        deslocamento
                );

        return new DeslocamentoRoteiroResposta(
                deslocamentoSalvo
        );
    }

    @GetMapping("/origem/{itemOrigemId}/destino/{itemDestinoId}")
    public ResponseEntity<DeslocamentoRoteiroResposta> buscarEntreItens(
            @PathVariable Long itemOrigemId,
            @PathVariable Long itemDestinoId,
            @RequestParam Long usuarioSolicitanteId) {

        return deslocamentoService
                .buscarEntreItens(
                        itemOrigemId,
                        itemDestinoId,
                        usuarioSolicitanteId
                )
                .map(DeslocamentoRoteiroResposta::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}