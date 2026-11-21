package br.com.locus.usuario;

import br.com.locus.usuario.dto.UsuarioResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public UsuarioResposta cadastrar(@Valid @RequestBody Usuario usuario) {

        Usuario usuarioSalvo = usuarioService.salvar(usuario);

        return new UsuarioResposta(usuarioSalvo);
    }

    @GetMapping
    public List<UsuarioResposta> listarTodos() {

        return usuarioService.listarTodos()
                .stream()
                .map(UsuarioResposta::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResposta> buscarPorId(
            @PathVariable Long id) {

        return usuarioService.buscarPorId(id)
                .map(usuario ->
                        ResponseEntity.ok(
                                new UsuarioResposta(usuario)
                        )
                )
                .orElse(ResponseEntity.notFound().build());
    }
}
