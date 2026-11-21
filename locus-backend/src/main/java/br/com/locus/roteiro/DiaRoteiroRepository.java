package br.com.locus.roteiro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaRoteiroRepository
        extends JpaRepository<DiaRoteiro, Long> {

    List<DiaRoteiro> findByViagemId(
            Long viagemId
    );

    List<DiaRoteiro> findByViagemIdOrderByDataAsc(
            Long viagemId
    );
}