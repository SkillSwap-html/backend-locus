package br.com.locus.compartilhamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LinkCompartilhamentoRepository
        extends JpaRepository<LinkCompartilhamento, Long> {

    Optional<LinkCompartilhamento>
    findByTokenAndAtivoTrue(String token);

    List<LinkCompartilhamento>
    findByViagemIdAndAtivoTrue(Long viagemId);

    Optional<LinkCompartilhamento>
    findFirstByViagemIdAndAtivoTrue(Long viagemId);
}