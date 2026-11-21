package br.com.locus.roteiro;

import br.com.locus.roteiro.dto.DiaRoteiroResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dias-roteiro")
public class DiaRoteiroController {

    private final DiaRoteiroService diaRoteiroService;

    public DiaRoteiroController(
            DiaRoteiroService diaRoteiroService) {

        this.diaRoteiroService =
                diaRoteiroService;
    }

    @PostMapping("/viagem/{viagemId}")
    public DiaRoteiroResposta cadastrar(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioId,
            @RequestBody DiaRoteiro diaRoteiro) {

        DiaRoteiro diaSalvo =
                diaRoteiroService.salvar(
                        viagemId,
                        usuarioId,
                        diaRoteiro
                );

        return new DiaRoteiroResposta(
                diaSalvo
        );
    }

    @GetMapping("/viagem/{viagemId}")
    public List<DiaRoteiroResposta> listarPorViagem(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioId) {

        return diaRoteiroService
                .listarPorViagem(
                        viagemId,
                        usuarioId
                )
                .stream()
                .map(DiaRoteiroResposta::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaRoteiroResposta> buscarPorId(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        return diaRoteiroService
                .buscarPorId(
                        id,
                        usuarioId
                )
                .map(DiaRoteiroResposta::new)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }
}