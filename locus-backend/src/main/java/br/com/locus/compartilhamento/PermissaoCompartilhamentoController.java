package br.com.locus.compartilhamento;

import br.com.locus.compartilhamento.dto.PermissaoCompartilhamentoResposta;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissoes-compartilhamento")
public class PermissaoCompartilhamentoController {

    private final PermissaoCompartilhamentoService permissaoService;

    public PermissaoCompartilhamentoController(
            PermissaoCompartilhamentoService permissaoService) {

        this.permissaoService = permissaoService;
    }

    @PostMapping("/viagem/{viagemId}/usuario/{usuarioId}")
    public PermissaoCompartilhamentoResposta compartilhar(
            @PathVariable Long viagemId,
            @PathVariable Long usuarioId,
            @RequestParam TipoPermissao tipoPermissao,
            @RequestParam Long usuarioSolicitanteId) {

        PermissaoCompartilhamento permissao =
                permissaoService.compartilhar(
                        viagemId,
                        usuarioId,
                        tipoPermissao,
                        usuarioSolicitanteId
                );

        return new PermissaoCompartilhamentoResposta(permissao);
    }

    @PutMapping("/{permissaoId}")
    public PermissaoCompartilhamentoResposta alterarPermissao(
            @PathVariable Long permissaoId,
            @RequestParam TipoPermissao tipoPermissao,
            @RequestParam Long usuarioSolicitanteId) {

        PermissaoCompartilhamento permissao =
                permissaoService.alterarPermissao(
                        permissaoId,
                        tipoPermissao,
                        usuarioSolicitanteId
                );

        return new PermissaoCompartilhamentoResposta(permissao);
    }

    @PutMapping("/{permissaoId}/revogar")
    public PermissaoCompartilhamentoResposta revogar(
            @PathVariable Long permissaoId,
            @RequestParam Long usuarioSolicitanteId) {

        PermissaoCompartilhamento permissao =
                permissaoService.revogar(
                        permissaoId,
                        usuarioSolicitanteId
                );

        return new PermissaoCompartilhamentoResposta(permissao);
    }

    @GetMapping("/viagem/{viagemId}")
    public List<PermissaoCompartilhamentoResposta> listarPorViagem(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        return permissaoService
                .listarPorViagem(
                        viagemId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(PermissaoCompartilhamentoResposta::new)
                .toList();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<PermissaoCompartilhamentoResposta> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam Long usuarioSolicitanteId) {

        return permissaoService
                .listarPorUsuario(
                        usuarioId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(PermissaoCompartilhamentoResposta::new)
                .toList();
    }
}