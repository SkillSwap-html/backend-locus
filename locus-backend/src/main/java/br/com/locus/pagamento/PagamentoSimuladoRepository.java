package br.com.locus.pagamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoSimuladoRepository
        extends JpaRepository<PagamentoSimulado, Long> {

    Optional<PagamentoSimulado> findByReservaSimuladaId(
            Long reservaSimuladaId
    );
}