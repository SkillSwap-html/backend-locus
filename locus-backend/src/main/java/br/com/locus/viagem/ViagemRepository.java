package br.com.locus.viagem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDate;

public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    List<Viagem> findByUsuarioId(Long usuarioId);

    List<Viagem> findByUsuarioIdAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            Long usuarioId,
            LocalDate dataFim,
            LocalDate dataInicio);
}
