package br.com.locus.lugar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FonteLugarDetalheRepository
        extends JpaRepository<FonteLugarDetalhe, Long> {

    Optional<FonteLugarDetalhe> findByLugarId(Long lugarId);

    boolean existsByLugarId(Long lugarId);
}