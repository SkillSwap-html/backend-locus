package br.com.locus.compartilhamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissaoCompartilhamentoRepository
        extends JpaRepository<PermissaoCompartilhamento, Long> {

    List<PermissaoCompartilhamento>
    findByViagemIdAndAtivoTrue(Long viagemId);

    List<PermissaoCompartilhamento>
    findByUsuarioIdAndAtivoTrue(Long usuarioId);

    Optional<PermissaoCompartilhamento>
    findByViagemIdAndUsuarioIdAndAtivoTrue(
            Long viagemId,
            Long usuarioId
    );
}