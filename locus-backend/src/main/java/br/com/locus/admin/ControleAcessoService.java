package br.com.locus.admin;

import br.com.locus.exception.AcessoNegadoException;
import br.com.locus.usuario.TipoUsuario;
import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;


@Service
public class ControleAcessoService {

    private final UsuarioRepository usuarioRepository;
    private final ViagemRepository viagemRepository;

    public ControleAcessoService(
            UsuarioRepository usuarioRepository,
            ViagemRepository viagemRepository) {

        this.usuarioRepository = usuarioRepository;
        this.viagemRepository = viagemRepository;
    }

    public Usuario buscarUsuario(Long usuarioId) {

        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuário não encontrado."
                        )
                );
    }

    public Usuario exigirAdministrador(Long usuarioId) {

        Usuario usuario = buscarUsuario(usuarioId);

        if (usuario.getTipoUsuario()
                != TipoUsuario.ADMINISTRADOR) {

            throw new AcessoNegadoException(
                    "Apenas administradores podem realizar esta operação."
            );
        }

        return usuario;
    }

    public Usuario exigirCuradorOuAdministrador(Long usuarioId) {

        Usuario usuario = buscarUsuario(usuarioId);

        if (usuario.getTipoUsuario() != TipoUsuario.CURADOR_CONTEUDO
                && usuario.getTipoUsuario() != TipoUsuario.ADMINISTRADOR) {

            throw new AcessoNegadoException(
                    "Apenas administradores ou curadores de conteúdo podem realizar esta operação."
            );
        }

        return usuario;
    }

    public Usuario exigirResponsavelTecnicoOuAdministrador(
            Long usuarioId) {

        Usuario usuario = buscarUsuario(usuarioId);

        if (usuario.getTipoUsuario()
                != TipoUsuario.RESPONSAVEL_TECNICO
                && usuario.getTipoUsuario()
                != TipoUsuario.ADMINISTRADOR) {

            throw new AcessoNegadoException(
                    "Apenas administradores ou responsáveis técnicos podem realizar esta operação."
            );
        }

        return usuario;
    }

    public Viagem exigirProprietarioViagem(
            Long viagemId,
            Long usuarioId) {

        Usuario usuario = buscarUsuario(usuarioId);

        Viagem viagem = viagemRepository.findById(viagemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Viagem não encontrada."
                        )
                );

        if (!viagem.getUsuario().getId()
                .equals(usuario.getId())) {

            throw new AcessoNegadoException(
                    "Apenas o proprietário da viagem pode realizar esta operação."
            );
        }

        return viagem;
    }

    public Usuario exigirMesmoUsuario(
            Long usuarioId,
            Long usuarioSolicitanteId) {

        Usuario usuario = buscarUsuario(usuarioId);
        Usuario usuarioSolicitante = buscarUsuario(usuarioSolicitanteId);

        if (!usuario.getId().equals(usuarioSolicitante.getId())) {
            throw new AcessoNegadoException(
                    "O usuário não possui permissão para acessar estes dados."
            );
        }

        return usuario;
    }
}