package br.com.locus.relatorio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadRelatorioRepository
        extends JpaRepository<DownloadRelatorio, Long> {

    List<DownloadRelatorio> findByRelatorioViagemIdOrderByDataDownloadDesc(
            Long relatorioId
    );
}