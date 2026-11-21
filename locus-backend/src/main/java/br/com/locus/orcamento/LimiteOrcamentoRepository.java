package br.com.locus.orcamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LimiteOrcamentoRepository
        extends JpaRepository<LimiteOrcamento, Long> {

    List<LimiteOrcamento> findByViagemId(Long viagemId);

    Optional<LimiteOrcamento> findByViagemIdAndCategoria(
            Long viagemId,
            CategoriaOrcamento categoria
    );

    boolean existsByViagemIdAndCategoria(
            Long viagemId,
            CategoriaOrcamento categoria
    );
}