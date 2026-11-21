package br.com.locus.preco;

import br.com.locus.preco.dto.OportunidadePrecoResposta;
import br.com.locus.preco.dto.ResumoHistoricoPreco;
import br.com.locus.preco.dto.SnapshotPrecoResposta;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/precos")
public class SnapshotPrecoController {

    private final SnapshotPrecoService snapshotPrecoService;

    public SnapshotPrecoController(
            SnapshotPrecoService snapshotPrecoService) {

        this.snapshotPrecoService = snapshotPrecoService;
    }

    @PostMapping("/snapshots")
    public SnapshotPrecoResposta registrar(
            @RequestBody SnapshotPreco snapshot,
            @RequestParam(required = false) Long usuarioSolicitanteId) {

        SnapshotPreco snapshotSalvo =
                snapshotPrecoService.registrar(
                        snapshot,
                        usuarioSolicitanteId
                );

        return new SnapshotPrecoResposta(
                snapshotSalvo
        );
    }

    @GetMapping("/historico")
    public List<SnapshotPrecoResposta> listarHistorico(
            @RequestParam String tipoOferta,
            @RequestParam Long ofertaId) {

        return snapshotPrecoService
                .listarUltimos30Dias(
                        tipoOferta,
                        ofertaId
                )
                .stream()
                .map(SnapshotPrecoResposta::new)
                .toList();
    }

    @GetMapping("/resumo")
    public ResumoHistoricoPreco consultarResumo(
            @RequestParam String tipoOferta,
            @RequestParam Long ofertaId) {

        return snapshotPrecoService
                .calcularResumo(
                        tipoOferta,
                        ofertaId
                );
    }

    @GetMapping("/oportunidade")
    public OportunidadePrecoResposta analisarOportunidade(
            @RequestParam String tipoOferta,
            @RequestParam Long ofertaId,
            @RequestParam(required = false) Long usuarioSolicitanteId) {

        return snapshotPrecoService
                .analisarOportunidade(
                        tipoOferta,
                        ofertaId,
                        usuarioSolicitanteId
                );
    }
}