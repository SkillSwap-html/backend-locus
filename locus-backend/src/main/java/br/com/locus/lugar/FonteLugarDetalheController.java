package br.com.locus.lugar;

import br.com.locus.lugar.dto.FonteLugarDetalheResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fontes-lugar")
public class FonteLugarDetalheController {

    private final FonteLugarDetalheService fonteLugarDetalheService;

    public FonteLugarDetalheController(
            FonteLugarDetalheService fonteLugarDetalheService) {

        this.fonteLugarDetalheService = fonteLugarDetalheService;
    }

    @PostMapping("/lugar/{lugarId}")
    public FonteLugarDetalheResposta cadastrar(
            @PathVariable Long lugarId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody FonteLugarDetalhe fonteLugarDetalhe) {

        FonteLugarDetalhe fonteSalva =
                fonteLugarDetalheService.salvar(
                        lugarId,
                        usuarioSolicitanteId,
                        fonteLugarDetalhe
                );

        return new FonteLugarDetalheResposta(
                fonteSalva
        );
    }

    @GetMapping("/lugar/{lugarId}")
    public ResponseEntity<FonteLugarDetalheResposta> buscarPorLugar(
            @PathVariable Long lugarId,
            @RequestParam Long usuarioSolicitanteId) {

        return fonteLugarDetalheService
                .buscarPorLugar(
                        lugarId,
                        usuarioSolicitanteId
                )
                .map(FonteLugarDetalheResposta::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}