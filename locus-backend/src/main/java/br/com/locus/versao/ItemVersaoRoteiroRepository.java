package br.com.locus.versao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemVersaoRoteiroRepository
        extends JpaRepository<ItemVersaoRoteiro, Long> {

    List<ItemVersaoRoteiro>
    findByVersaoRoteiroIdOrderByDataDiaAscOrdemAsc(
            Long versaoRoteiroId
    );
}