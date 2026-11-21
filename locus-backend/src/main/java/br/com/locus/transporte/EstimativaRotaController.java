package br.com.locus.transporte;

import br.com.locus.transporte.dto.EstimativaRotaResposta;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estimativas-rota")
public class EstimativaRotaController {

    private final EstimativaRotaService estimativaRotaService;

    public EstimativaRotaController(
            EstimativaRotaService estimativaRotaService) {

        this.estimativaRotaService = estimativaRotaService;
    }

    @PostMapping("/viagem/{viagemId}")
    public EstimativaRotaResposta cadastrar(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody EstimativaRota estimativaRota) {

        EstimativaRota estimativaSalva =
                estimativaRotaService.salvar(
                        viagemId,
                        usuarioSolicitanteId,
                        estimativaRota
                );

        return new EstimativaRotaResposta(estimativaSalva);
    }

    @GetMapping("/viagem/{viagemId}")
    public List<EstimativaRotaResposta> listarPorViagem(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        return estimativaRotaService
                .listarPorViagem(
                        viagemId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(EstimativaRotaResposta::new)
                .toList();
    }
}