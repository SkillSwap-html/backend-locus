package br.com.locus.perfil;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilVeiculoRepository extends JpaRepository<PerfilVeiculo, Long> {

    Optional<PerfilVeiculo> findByPerfilViajanteId(Long perfilViajanteId);

    boolean existsByPerfilViajanteId(Long perfilViajanteId);
}