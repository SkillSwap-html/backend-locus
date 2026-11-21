package br.com.locus.relatorio;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DownloadRelatorioService {

    private final DownloadRelatorioRepository downloadRelatorioRepository;

    public DownloadRelatorioService(
            DownloadRelatorioRepository downloadRelatorioRepository) {

        this.downloadRelatorioRepository =
                downloadRelatorioRepository;
    }

    public DownloadRelatorio registrarDownload(
            RelatorioViagem relatorioViagem) {

        DownloadRelatorio download =
                new DownloadRelatorio();

        download.setRelatorioViagem(relatorioViagem);
        download.setDataDownload(LocalDateTime.now());

        return downloadRelatorioRepository.save(download);
    }

    public List<DownloadRelatorio> listarPorRelatorio(
            Long relatorioId) {

        return downloadRelatorioRepository
                .findByRelatorioViagemIdOrderByDataDownloadDesc(
                        relatorioId
                );
    }
}