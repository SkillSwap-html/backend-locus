package br.com.locus.versao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VersaoRoteiroRepository
        extends JpaRepository<VersaoRoteiro, Long> {

    List<VersaoRoteiro>
    findByViagemIdOrderByNumeroVersaoDesc(
            Long viagemId
    );

    Optional<VersaoRoteiro>
    findFirstByViagemIdOrderByNumeroVersaoDesc(
            Long viagemId
    );
}