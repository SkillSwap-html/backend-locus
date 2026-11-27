package br.com.locus.usuario;

import br.com.locus.usuario.dto.UsuarioResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

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
    @PreAuthorize("@securityMode.permitOrHasAdmin(authentication)")
    public List<UsuarioResposta> listarTodos() {

        return usuarioService.listarTodos()
                .stream()
                .map(UsuarioResposta::new)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityMode.permitOrSameUser(authentication, #id)")
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
