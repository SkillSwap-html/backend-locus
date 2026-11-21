package br.com.locus.perfil;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenciaViagemRepository extends JpaRepository<PreferenciaViagem, Long> {

    Optional<PreferenciaViagem> findByPerfilViajanteId(Long perfilViajanteId);

    boolean existsByPerfilViajanteId(Long perfilViajanteId);
}