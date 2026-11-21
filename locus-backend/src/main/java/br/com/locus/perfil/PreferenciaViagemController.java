package br.com.locus.perfil;

import br.com.locus.perfil.dto.PreferenciaViagemResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferencias-viagem")
public class PreferenciaViagemController {

    private final PreferenciaViagemService preferenciaViagemService;

    public PreferenciaViagemController(
            PreferenciaViagemService preferenciaViagemService) {

        this.preferenciaViagemService = preferenciaViagemService;
    }

    @RequestMapping(value = "/perfil/{perfilId}", method = {RequestMethod.POST, RequestMethod.PUT})
    public PreferenciaViagemResposta cadastrar(
            @PathVariable Long perfilId,
            @RequestParam Long usuarioSolicitanteId,
            @RequestBody PreferenciaViagem preferenciaViagem) {

        PreferenciaViagem preferenciaSalva =
                preferenciaViagemService.salvar(
                        perfilId,
                        usuarioSolicitanteId,
                        preferenciaViagem
                );

        return new PreferenciaViagemResposta(preferenciaSalva);
    }

    @GetMapping("/perfil/{perfilId}")
    public ResponseEntity<PreferenciaViagemResposta> buscarPorPerfil(
            @PathVariable Long perfilId,
            @RequestParam Long usuarioSolicitanteId) {

        return ResponseEntity.ok(
                new PreferenciaViagemResposta(
                        preferenciaViagemService.buscarOuCriarPadrao(
                                perfilId,
                                usuarioSolicitanteId
                        )));
    }
}
