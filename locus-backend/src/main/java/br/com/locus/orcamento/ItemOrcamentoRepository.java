package br.com.locus.orcamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemOrcamentoRepository
        extends JpaRepository<ItemOrcamento, Long> {

    List<ItemOrcamento> findByViagemId(Long viagemId);

    List<ItemOrcamento> findByTipoOfertaIsNotNullAndOfertaIdIsNotNull();

    List<ItemOrcamento> findByViagemIdAndCategoria(
            Long viagemId,
            CategoriaOrcamento categoria
    );

    boolean existsByViagemIdAndCategoria(Long viagemId, CategoriaOrcamento categoria);

    boolean existsByEstimativaRotaId(Long estimativaRotaId);
}
