package br.com.locus.reserva;

import br.com.locus.reserva.dto.ReservaSimuladaResposta;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas-simuladas")
public class ReservaSimuladaController {

    private final ReservaSimuladaService reservaSimuladaService;

    public ReservaSimuladaController(
            ReservaSimuladaService reservaSimuladaService) {

        this.reservaSimuladaService = reservaSimuladaService;
    }

    @PostMapping("/viagem/{viagemId}")
    public ReservaSimuladaResposta criar(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        ReservaSimulada reserva =
                reservaSimuladaService.criar(
                        viagemId,
                        usuarioSolicitanteId
                );

        return new ReservaSimuladaResposta(reserva);
    }

    @PostMapping("/{reservaId}/preparar-pagamento")
    public ReservaSimuladaResposta prepararPagamento(
            @PathVariable Long reservaId,
            @RequestParam Long usuarioSolicitanteId) {

        ReservaSimulada reserva =
                reservaSimuladaService.prepararPagamento(
                        reservaId,
                        usuarioSolicitanteId
                );

        return new ReservaSimuladaResposta(reserva);
    }

    @PostMapping("/{reservaId}/confirmar")
    public ReservaSimuladaResposta confirmar(
            @PathVariable Long reservaId,
            @RequestParam Long usuarioSolicitanteId) {

        ReservaSimulada reserva =
                reservaSimuladaService.confirmar(
                        reservaId,
                        usuarioSolicitanteId
                );

        return new ReservaSimuladaResposta(reserva);
    }

    @PostMapping("/{reservaId}/cancelar")
    public ReservaSimuladaResposta cancelar(
            @PathVariable Long reservaId,
            @RequestParam Long usuarioSolicitanteId) {

        ReservaSimulada reserva =
                reservaSimuladaService.cancelar(
                        reservaId,
                        usuarioSolicitanteId
                );

        return new ReservaSimuladaResposta(reserva);
    }

    @GetMapping("/{reservaId}")
    public ReservaSimuladaResposta buscarPorId(
            @PathVariable Long reservaId,
            @RequestParam Long usuarioSolicitanteId) {

        ReservaSimulada reserva =
                reservaSimuladaService.buscarPorId(
                        reservaId,
                        usuarioSolicitanteId
                );

        return new ReservaSimuladaResposta(reserva);
    }

    @GetMapping("/viagem/{viagemId}")
    public List<ReservaSimuladaResposta> listarPorViagem(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        return reservaSimuladaService
                .listarPorViagem(
                        viagemId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(ReservaSimuladaResposta::new)
                .toList();
    }
}