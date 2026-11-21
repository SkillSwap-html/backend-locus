package br.com.locus.usuario;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario salvar(Usuario usuario) {

        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new RuntimeException("O nome é obrigatório.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new RuntimeException("O e-mail é obrigatório.");
        }

        if (usuario.getFirebaseUid() == null || usuario.getFirebaseUid().isBlank()) {
            throw new RuntimeException("O firebaseUid é obrigatório.");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException(
                    "Já existe um usuário cadastrado com este e-mail."
            );
        }

        if (usuarioRepository.existsByFirebaseUid(usuario.getFirebaseUid())) {
            throw new RuntimeException(
                    "Já existe um usuário cadastrado com este firebaseUid."
            );
        }

        usuario.setTipoUsuario(TipoUsuario.VIAJANTE);

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorFirebaseUid(String firebaseUid) {
        return usuarioRepository.findByFirebaseUid(firebaseUid);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}