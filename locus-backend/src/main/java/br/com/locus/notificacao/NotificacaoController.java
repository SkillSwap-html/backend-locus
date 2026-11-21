package br.com.locus.notificacao;

import br.com.locus.notificacao.dto.NotificacaoResposta;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(
            NotificacaoService notificacaoService) {

        this.notificacaoService = notificacaoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<NotificacaoResposta> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam Long usuarioSolicitanteId) {

        return notificacaoService.listarPorUsuario(
                usuarioId,
                usuarioSolicitanteId
        );
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    public List<NotificacaoResposta> listarNaoLidas(
            @PathVariable Long usuarioId,
            @RequestParam Long usuarioSolicitanteId) {

        return notificacaoService.listarNaoLidas(
                usuarioId,
                usuarioSolicitanteId
        );
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas/quantidade")
    public long contarNaoLidas(
            @PathVariable Long usuarioId,
            @RequestParam Long usuarioSolicitanteId) {

        return notificacaoService.contarNaoLidas(
                usuarioId,
                usuarioSolicitanteId
        );
    }

    @PutMapping("/usuario/{usuarioId}/ler-todas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarTodasComoLidas(
            @PathVariable Long usuarioId,
            @RequestParam Long usuarioSolicitanteId) {

        notificacaoService.marcarTodasComoLidas(
                usuarioId,
                usuarioSolicitanteId
        );
    }

    @PutMapping("/{notificacaoId}/ler")
    public NotificacaoResposta marcarComoLida(
            @PathVariable Long notificacaoId,
            @RequestParam Long usuarioId,
            @RequestParam Long usuarioSolicitanteId) {

        return notificacaoService.marcarComoLida(
                notificacaoId,
                usuarioId,
                usuarioSolicitanteId
        );
    }
}