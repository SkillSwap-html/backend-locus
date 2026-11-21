package br.com.locus.perfil;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilViajanteRepository extends JpaRepository<PerfilViajante, Long> {

    Optional<PerfilViajante> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);
}