package br.com.locus.reserva;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservaSimuladaRepository
        extends JpaRepository<ReservaSimulada, Long> {

    List<ReservaSimulada> findByViagemId(Long viagemId);

    Optional<ReservaSimulada> findByProtocolo(String protocolo);

    boolean existsByProtocolo(String protocolo);

    Optional<ReservaSimulada> findFirstByViagemIdAndStatusNotOrderByIdDesc(
            Long viagemId,
            StatusReservaSimulada status
    );
}