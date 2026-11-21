package br.com.locus.orcamento;

import br.com.locus.orcamento.dto.ResumoOrcamento;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orcamento")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    public OrcamentoController(
            OrcamentoService orcamentoService) {

        this.orcamentoService = orcamentoService;
    }

    @GetMapping("/viagem/{viagemId}/resumo")
    public ResumoOrcamento gerarResumo(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        return orcamentoService.gerarResumo(
                viagemId,
                usuarioSolicitanteId
        );
    }
}