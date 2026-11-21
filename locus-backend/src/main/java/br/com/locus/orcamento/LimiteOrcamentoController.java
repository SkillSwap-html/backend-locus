package br.com.locus.orcamento;

import br.com.locus.orcamento.dto.AtualizarLimiteOrcamentoRequest;
import br.com.locus.orcamento.dto.LimiteOrcamentoResposta;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/limites-orcamento")
public class LimiteOrcamentoController {

    private final LimiteOrcamentoService limiteOrcamentoService;

    public LimiteOrcamentoController(
            LimiteOrcamentoService limiteOrcamentoService) {

        this.limiteOrcamentoService =
                limiteOrcamentoService;
    }

    @PostMapping("/viagem/{viagemId}")
    public LimiteOrcamentoResposta salvar(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody LimiteOrcamento limiteOrcamento) {

        LimiteOrcamento limite =
                limiteOrcamentoService.salvar(
                        viagemId,
                        usuarioSolicitanteId,
                        limiteOrcamento
                );

        return new LimiteOrcamentoResposta(limite);
    }

    @PutMapping("/{limiteId}")
    public LimiteOrcamentoResposta atualizar(
            @PathVariable Long limiteId,
            @RequestBody AtualizarLimiteOrcamentoRequest request,
            @RequestParam Long usuarioSolicitanteId) {

        LimiteOrcamento limite =
                limiteOrcamentoService.atualizar(
                        limiteId,
                        request,
                        usuarioSolicitanteId
                );

        return new LimiteOrcamentoResposta(limite);
    }

    @GetMapping("/viagem/{viagemId}")
    public List<LimiteOrcamentoResposta> listarPorViagem(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        return limiteOrcamentoService
                .listarPorViagem(
                        viagemId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(LimiteOrcamentoResposta::new)
                .toList();
    }
}