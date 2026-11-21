package br.com.locus.versao;

import br.com.locus.versao.dto.ItemVersaoRoteiroResposta;
import br.com.locus.versao.dto.VersaoRoteiroResposta;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/versoes-roteiro")
public class VersaoRoteiroController {

    private final VersaoRoteiroService versaoRoteiroService;

    public VersaoRoteiroController(
            VersaoRoteiroService versaoRoteiroService) {

        this.versaoRoteiroService = versaoRoteiroService;
    }

    @PostMapping("/viagem/{viagemId}")
    public VersaoRoteiroResposta criarVersao(
            @PathVariable Long viagemId,
            @RequestParam(required = false) String descricao,
            @RequestParam Long usuarioSolicitanteId) {

        VersaoRoteiro versao =
                versaoRoteiroService.criarVersao(
                        viagemId,
                        descricao,
                        usuarioSolicitanteId
                );

        return new VersaoRoteiroResposta(versao);
    }

    @GetMapping("/viagem/{viagemId}")
    public List<VersaoRoteiroResposta> listarPorViagem(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        return versaoRoteiroService
                .listarPorViagem(
                        viagemId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(VersaoRoteiroResposta::new)
                .toList();
    }

    @GetMapping("/{versaoId}/itens")
    public List<ItemVersaoRoteiroResposta> listarItensDaVersao(
            @PathVariable Long versaoId,
            @RequestParam Long usuarioSolicitanteId) {

        return versaoRoteiroService
                .listarItensDaVersao(
                        versaoId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(ItemVersaoRoteiroResposta::new)
                .toList();
    }

    @GetMapping("/comparar")
    public List<ComparacaoVersaoRoteiro> compararVersoes(
            @RequestParam Long versaoAntigaId,
            @RequestParam Long versaoNovaId,
            @RequestParam Long usuarioSolicitanteId) {

        return versaoRoteiroService.compararVersoes(
                versaoAntigaId,
                versaoNovaId,
                usuarioSolicitanteId
        );
    }
}