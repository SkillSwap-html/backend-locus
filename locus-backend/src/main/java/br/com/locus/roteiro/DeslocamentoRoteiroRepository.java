package br.com.locus.roteiro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeslocamentoRoteiroRepository
        extends JpaRepository<DeslocamentoRoteiro, Long> {

    Optional<DeslocamentoRoteiro>
    findByItemOrigemIdAndItemDestinoId(
            Long itemOrigemId,
            Long itemDestinoId
    );

    boolean existsByItemOrigemIdAndItemDestinoId(
            Long itemOrigemId,
            Long itemDestinoId
    );

    List<DeslocamentoRoteiro>
    findByItemOrigemIdOrItemDestinoId(
            Long itemOrigemId,
            Long itemDestinoId
    );
}