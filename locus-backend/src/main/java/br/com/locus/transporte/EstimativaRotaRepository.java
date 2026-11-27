package br.com.locus.transporte;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstimativaRotaRepository
        extends JpaRepository<EstimativaRota, Long> {

    List<EstimativaRota> findByViagemId(Long viagemId);

    Optional<EstimativaRota>
    findFirstByViagemIdOrderByDataConsultaDesc(Long viagemId);
}