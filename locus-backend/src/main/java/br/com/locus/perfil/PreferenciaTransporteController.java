package br.com.locus.perfil;

import br.com.locus.perfil.dto.PreferenciaTransporteResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferencias-transporte")
public class PreferenciaTransporteController {

    private final PreferenciaTransporteService preferenciaTransporteService;

    public PreferenciaTransporteController(
            PreferenciaTransporteService preferenciaTransporteService) {

        this.preferenciaTransporteService = preferenciaTransporteService;
    }

    @PostMapping("/perfil/{perfilId}")
    public PreferenciaTransporteResposta cadastrar(
            @PathVariable Long perfilId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody PreferenciaTransporte preferenciaTransporte) {

        PreferenciaTransporte preferenciaSalva =
                preferenciaTransporteService.salvar(
                        perfilId,
                        usuarioSolicitanteId,
                        preferenciaTransporte
                );

        return new PreferenciaTransporteResposta(preferenciaSalva);
    }

    @GetMapping("/perfil/{perfilId}")
    public ResponseEntity<PreferenciaTransporteResposta> buscarPorPerfil(
            @PathVariable Long perfilId,
            @RequestParam Long usuarioSolicitanteId) {

        return preferenciaTransporteService
                .buscarPorPerfil(
                        perfilId,
                        usuarioSolicitanteId
                )
                .map(preferencia -> ResponseEntity.ok(
                        new PreferenciaTransporteResposta(preferencia)))
                .orElse(ResponseEntity.notFound().build());
    }
}