package br.com.locus.roteiro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRoteiroRepository
        extends JpaRepository<ItemRoteiro, Long> {

    List<ItemRoteiro>
    findByDiaRoteiroIdOrderByOrdemAsc(
            Long diaRoteiroId
    );

    Optional<ItemRoteiro>
    findFirstByDiaRoteiroIdOrderByOrdemDesc(
            Long diaRoteiroId
    );
}