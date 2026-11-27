package br.com.locus.admin;

import br.com.locus.exception.AcessoNegadoException;
import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import br.com.locus.viagem.ViagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControleAcessoServiceTest {

    private UsuarioRepository usuarioRepository;
    private ViagemRepository viagemRepository;
    private ControleAcessoService controleAcessoService;

    @BeforeEach
    void prepararTeste() {
        usuarioRepository = mock(UsuarioRepository.class);
        viagemRepository = mock(ViagemRepository.class);

        controleAcessoService = new ControleAcessoService(
                usuarioRepository,
                viagemRepository
        );
    }

    @Test
    void devePermitirAcessoQuandoForOMesmoUsuario() {
        Usuario usuario = mock(Usuario.class);

        when(usuario.getId()).thenReturn(2L);
        when(usuarioRepository.findById(2L))
                .thenReturn(Optional.of(usuario));

        assertDoesNotThrow(() ->
                controleAcessoService.exigirMesmoUsuario(2L, 2L)
        );
    }

    @Test
    void deveNegarAcessoQuandoForOutroUsuario() {
        Usuario usuario = mock(Usuario.class);
        Usuario outroUsuario = mock(Usuario.class);

        when(usuario.getId()).thenReturn(2L);
        when(outroUsuario.getId()).thenReturn(3L);

        when(usuarioRepository.findById(2L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.findById(3L))
                .thenReturn(Optional.of(outroUsuario));

        assertThrows(
                AcessoNegadoException.class,
                () -> controleAcessoService.exigirMesmoUsuario(2L, 3L)
        );
    }
}