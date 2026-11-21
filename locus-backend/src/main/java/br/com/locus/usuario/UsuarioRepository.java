package br.com.locus.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByFirebaseUid(String firebaseUid);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByFirebaseUid(String firebaseUid);

    long countByTipoUsuario(TipoUsuario tipoUsuario);
}