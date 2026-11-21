package br.com.locus.usuario;

import br.com.locus.admin.AdminEmailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AdminEmailRepository adminEmailRepository;
    private final String adminInitialEmail;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            AdminEmailRepository adminEmailRepository,
            @Value("${app.admin.initial-email:}") String adminInitialEmail) {
        this.usuarioRepository = usuarioRepository;
        this.adminEmailRepository = adminEmailRepository;
        this.adminInitialEmail = adminInitialEmail == null ? "" : adminInitialEmail.trim();
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

        if (usuario.getEmail().equalsIgnoreCase(adminInitialEmail)
                && adminInitialEmail.contains("@")) {
            adminEmailRepository.findByEmailIgnoreCase(adminInitialEmail)
                    .orElseGet(() -> adminEmailRepository.save(
                            new br.com.locus.admin.AdminEmail(adminInitialEmail.toLowerCase())
                    ));
        }

        Usuario existente = usuarioRepository.findByFirebaseUid(usuario.getFirebaseUid()).orElse(null);
        Usuario porEmail = usuarioRepository.findByEmailIgnoreCase(usuario.getEmail()).orElse(null);
        if (existente == null && porEmail != null) {
            existente = porEmail;
        }
        if (existente != null) {
            existente.setFirebaseUid(usuario.getFirebaseUid());
            existente.setNome(usuario.getNome());
            existente.setEmail(usuario.getEmail());
            existente.setTermosAceitos(usuario.isTermosAceitos());
            if (ehAdministrador(usuario.getEmail())) {
                existente.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            }
            return usuarioRepository.save(existente);
        }

        usuario.setTipoUsuario(
                ehAdministrador(usuario.getEmail())
                        ? TipoUsuario.ADMINISTRADOR
                        : TipoUsuario.VIAJANTE
        );
        return usuarioRepository.save(usuario);
    }

    private boolean ehAdministrador(String email) {
        return email != null
                && (email.equalsIgnoreCase(adminInitialEmail)
                || adminEmailRepository.existsByEmailIgnoreCase(email));
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
