package br.com.locus.preco;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SnapshotPrecoRepository
        extends JpaRepository<SnapshotPreco, Long> {

    List<SnapshotPreco>
    findByTipoOfertaAndOfertaIdAndDataConsultaBetweenOrderByDataConsultaAsc(
            String tipoOferta,
            Long ofertaId,
            LocalDateTime dataInicial,
            LocalDateTime dataFinal
    );
}