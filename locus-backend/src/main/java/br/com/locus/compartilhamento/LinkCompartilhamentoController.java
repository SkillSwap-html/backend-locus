package br.com.locus.compartilhamento;

import br.com.locus.compartilhamento.dto.LinkCompartilhamentoResposta;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import br.com.locus.compartilhamento.dto.ViagemCompartilhadaResposta;
import br.com.locus.compartilhamento.dto.DiaRoteiroCompartilhadoResposta;
import br.com.locus.orcamento.dto.ResumoOrcamento;
import br.com.locus.compartilhamento.dto.EstimativaRotaCompartilhadaResposta;

@RestController
@RequestMapping("/api/links-compartilhamento")
public class LinkCompartilhamentoController {

    private final LinkCompartilhamentoService linkService;

    public LinkCompartilhamentoController(
            LinkCompartilhamentoService linkService) {

        this.linkService = linkService;
    }

    @PostMapping("/viagem/{viagemId}")
    public LinkCompartilhamentoResposta criarLink(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        LinkCompartilhamento link =
                linkService.criarLink(
                        viagemId,
                        usuarioSolicitanteId
                );

        return new LinkCompartilhamentoResposta(link);
    }

    @PutMapping("/{linkId}/revogar")
    public LinkCompartilhamentoResposta revogarLink(
            @PathVariable Long linkId,
            @RequestParam Long usuarioSolicitanteId) {

        LinkCompartilhamento link =
                linkService.revogarLink(
                        linkId,
                        usuarioSolicitanteId
                );

        return new LinkCompartilhamentoResposta(link);
    }

    @GetMapping("/viagem/{viagemId}")
    public List<LinkCompartilhamentoResposta> listarPorViagem(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        return linkService
                .listarPorViagem(
                        viagemId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(LinkCompartilhamentoResposta::new)
                .toList();
    }

    @GetMapping("/acesso/{token}")
    public LinkCompartilhamentoResposta acessarPorToken(
            @PathVariable String token) {

        LinkCompartilhamento link =
                linkService.buscarPorToken(token);

        return new LinkCompartilhamentoResposta(link);
    }

    @GetMapping("/acesso/{token}/viagem")
    public ViagemCompartilhadaResposta visualizarViagem(
            @PathVariable String token) {

        LinkCompartilhamento link =
                linkService.buscarPorToken(token);

        return new ViagemCompartilhadaResposta(
                link.getViagem()
        );
    }

    @GetMapping("/acesso/{token}/roteiro")
    public List<DiaRoteiroCompartilhadoResposta> visualizarRoteiro(
            @PathVariable String token) {

        return linkService.buscarRoteiroPorToken(token);
    }
    @GetMapping("/acesso/{token}/orcamento")
    public ResumoOrcamento visualizarOrcamento(
            @PathVariable String token) {

        return linkService.buscarOrcamentoPorToken(token);
    }

    @GetMapping("/acesso/{token}/transporte")
    public EstimativaRotaCompartilhadaResposta visualizarTransporte(
            @PathVariable String token) {

        return linkService.buscarEstimativaRotaPorToken(token);
    }
}