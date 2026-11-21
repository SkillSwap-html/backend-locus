package br.com.locus.perfil;

import br.com.locus.perfil.dto.PerfilVeiculoResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfis-veiculo")
public class PerfilVeiculoController {

    private final PerfilVeiculoService perfilVeiculoService;

    public PerfilVeiculoController(
            PerfilVeiculoService perfilVeiculoService) {

        this.perfilVeiculoService = perfilVeiculoService;
    }

    @PostMapping("/perfil/{perfilId}")
    public PerfilVeiculoResposta cadastrar(
            @PathVariable Long perfilId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody PerfilVeiculo perfilVeiculo) {

        PerfilVeiculo perfilSalvo =
                perfilVeiculoService.salvar(
                        perfilId,
                        usuarioSolicitanteId,
                        perfilVeiculo
                );

        return new PerfilVeiculoResposta(perfilSalvo);
    }

    @GetMapping("/perfil/{perfilId}")
    public ResponseEntity<PerfilVeiculoResposta> buscarPorPerfil(
            @PathVariable Long perfilId,
            @RequestParam Long usuarioSolicitanteId) {

        return perfilVeiculoService
                .buscarPorPerfil(
                        perfilId,
                        usuarioSolicitanteId
                )
                .map(perfil -> ResponseEntity.ok(
                        new PerfilVeiculoResposta(perfil)))
                .orElse(ResponseEntity.notFound().build());
    }
}