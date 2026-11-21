package br.com.locus.pagamento;

import br.com.locus.pagamento.dto.PagamentoSimuladoResposta;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos-simulados")
public class PagamentoSimuladoController {

    private final PagamentoSimuladoService pagamentoSimuladoService;

    public PagamentoSimuladoController(
            PagamentoSimuladoService pagamentoSimuladoService) {

        this.pagamentoSimuladoService =
                pagamentoSimuladoService;
    }

    @PostMapping("/reserva/{reservaId}/iniciar")
    public PagamentoSimuladoResposta iniciar(
            @PathVariable Long reservaId,
            @RequestParam Long usuarioSolicitanteId) {

        PagamentoSimulado pagamento =
                pagamentoSimuladoService.iniciar(
                        reservaId,
                        usuarioSolicitanteId
                );

        return new PagamentoSimuladoResposta(pagamento);
    }

    @PostMapping("/{pagamentoId}/concluir")
    public PagamentoSimuladoResposta concluir(
            @PathVariable Long pagamentoId,
            @RequestParam Long usuarioSolicitanteId) {

        PagamentoSimulado pagamento =
                pagamentoSimuladoService.concluir(
                        pagamentoId,
                        usuarioSolicitanteId
                );

        return new PagamentoSimuladoResposta(pagamento);
    }

    @PostMapping("/{pagamentoId}/interromper")
    public PagamentoSimuladoResposta interromper(
            @PathVariable Long pagamentoId,
            @RequestParam Long usuarioSolicitanteId) {

        PagamentoSimulado pagamento =
                pagamentoSimuladoService.interromper(
                        pagamentoId,
                        usuarioSolicitanteId
                );

        return new PagamentoSimuladoResposta(pagamento);
    }

    @GetMapping("/reserva/{reservaId}")
    public PagamentoSimuladoResposta buscarPorReserva(
            @PathVariable Long reservaId,
            @RequestParam Long usuarioSolicitanteId) {

        PagamentoSimulado pagamento =
                pagamentoSimuladoService.buscarPorReserva(
                        reservaId,
                        usuarioSolicitanteId
                );

        return new PagamentoSimuladoResposta(pagamento);
    }

    @GetMapping("/{pagamentoId}")
    public PagamentoSimuladoResposta buscarPorId(
            @PathVariable Long pagamentoId,
            @RequestParam Long usuarioSolicitanteId) {

        PagamentoSimulado pagamento =
                pagamentoSimuladoService.buscarPorId(
                        pagamentoId,
                        usuarioSolicitanteId
                );

        return new PagamentoSimuladoResposta(pagamento);
    }
}