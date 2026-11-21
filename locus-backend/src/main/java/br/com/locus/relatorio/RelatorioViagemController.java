package br.com.locus.relatorio;

import br.com.locus.relatorio.dto.DownloadRelatorioResposta;
import br.com.locus.relatorio.dto.RelatorioViagemResposta;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioViagemController {

    private final RelatorioViagemService relatorioViagemService;
    private final DownloadRelatorioService downloadRelatorioService;

    public RelatorioViagemController(
            RelatorioViagemService relatorioViagemService,
            DownloadRelatorioService downloadRelatorioService) {

        this.relatorioViagemService = relatorioViagemService;
        this.downloadRelatorioService = downloadRelatorioService;
    }

    @PostMapping("/reserva/{reservaId}")
    public RelatorioViagemResposta gerar(
            @PathVariable Long reservaId,
            @RequestParam Long usuarioSolicitanteId) {

        RelatorioViagem relatorio =
                relatorioViagemService.gerar(
                        reservaId,
                        usuarioSolicitanteId
                );

        return new RelatorioViagemResposta(relatorio);
    }

    @GetMapping("/{relatorioId}")
    public RelatorioViagemResposta buscarPorId(
            @PathVariable Long relatorioId,
            @RequestParam Long usuarioSolicitanteId) {

        RelatorioViagem relatorio =
                relatorioViagemService.buscarPorId(
                        relatorioId,
                        usuarioSolicitanteId
                );

        return new RelatorioViagemResposta(relatorio);
    }

    @GetMapping("/reserva/{reservaId}")
    public RelatorioViagemResposta buscarPorReserva(
            @PathVariable Long reservaId,
            @RequestParam Long usuarioSolicitanteId) {

        RelatorioViagem relatorio =
                relatorioViagemService.buscarPorReserva(
                        reservaId,
                        usuarioSolicitanteId
                );

        return new RelatorioViagemResposta(relatorio);
    }

    @GetMapping("/{relatorioId}/pdf")
    public ResponseEntity<byte[]> baixarPdf(
            @PathVariable Long relatorioId,
            @RequestParam Long usuarioSolicitanteId) {

        RelatorioViagem relatorio =
                relatorioViagemService.buscarPorId(
                        relatorioId,
                        usuarioSolicitanteId
                );

        downloadRelatorioService.registrarDownload(relatorio);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + relatorio.getNomeArquivo()
                                + "\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(relatorio.getArquivoPdf());
    }

    @GetMapping("/{relatorioId}/downloads")
    public List<DownloadRelatorioResposta> listarDownloads(
            @PathVariable Long relatorioId,
            @RequestParam Long usuarioSolicitanteId) {

        relatorioViagemService.buscarPorId(
                relatorioId,
                usuarioSolicitanteId
        );

        return downloadRelatorioService
                .listarPorRelatorio(relatorioId)
                .stream()
                .map(DownloadRelatorioResposta::new)
                .toList();
    }
}