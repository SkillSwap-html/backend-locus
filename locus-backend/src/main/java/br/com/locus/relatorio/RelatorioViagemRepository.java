package br.com.locus.relatorio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RelatorioViagemRepository
        extends JpaRepository<RelatorioViagem, Long> {

    Optional<RelatorioViagem>
    findByReservaSimuladaId(Long reservaSimuladaId);

    boolean existsByReservaSimuladaId(
            Long reservaSimuladaId);
}