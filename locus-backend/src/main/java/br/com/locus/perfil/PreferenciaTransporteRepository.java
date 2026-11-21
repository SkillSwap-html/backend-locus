package br.com.locus.perfil;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenciaTransporteRepository extends JpaRepository<PreferenciaTransporte, Long> {

    Optional<PreferenciaTransporte> findByPerfilViajanteId(Long perfilViajanteId);

    boolean existsByPerfilViajanteId(Long perfilViajanteId);
}