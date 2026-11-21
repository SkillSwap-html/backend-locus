package br.com.locus.preco;

import br.com.locus.orcamento.FonteItemOrcamento;
import br.com.locus.orcamento.ItemOrcamento;
import br.com.locus.orcamento.ItemOrcamentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Mantém o histórico das ofertas que já foram escolhidas no orçamento.
 * A rotina não cria preço novo: registra o último valor observado pelo Locus
 * e deixa claro que a disponibilidade/preço deve ser confirmado antes da
 * simulação.
 */
@Component
public class SnapshotPrecoJob {

    private static final Logger log = LoggerFactory.getLogger(SnapshotPrecoJob.class);

    private final ItemOrcamentoRepository itemOrcamentoRepository;
    private final SnapshotPrecoService snapshotPrecoService;
    private final boolean habilitado;

    public SnapshotPrecoJob(
            ItemOrcamentoRepository itemOrcamentoRepository,
            SnapshotPrecoService snapshotPrecoService,
            @Value("${app.precos.snapshot-enabled:true}") boolean habilitado) {
        this.itemOrcamentoRepository = itemOrcamentoRepository;
        this.snapshotPrecoService = snapshotPrecoService;
        this.habilitado = habilitado;
    }

    @Scheduled(
            initialDelayString = "${app.precos.snapshot-initial-delay-ms:60000}",
            fixedDelayString = "${app.precos.snapshot-interval-ms:86400000}"
    )
    public void registrarSnapshotsDasOfertasEscolhidas() {
        if (!habilitado) return;

        Set<String> processadas = new HashSet<>();
        for (ItemOrcamento item : itemOrcamentoRepository.findByTipoOfertaIsNotNullAndOfertaIdIsNotNull()) {
            String chave = item.getTipoOferta() + ":" + item.getOfertaId();
            if (!processadas.add(chave) || item.getValor() == null || item.getValor().signum() <= 0) continue;

            SnapshotPreco snapshot = new SnapshotPreco();
            snapshot.setTipoOferta(item.getTipoOferta());
            snapshot.setOfertaId(item.getOfertaId());
            snapshot.setPreco(item.getValor());
            snapshot.setMoeda("BRL");
            snapshot.setFonte(fonte(item.getFonte()));
            try {
                snapshotPrecoService.registrar(snapshot, null);
            } catch (RuntimeException exception) {
                log.warn("Não foi possível atualizar o snapshot da oferta {}: {}", chave, exception.getMessage());
            }
        }
    }

    private TipoFontePreco fonte(FonteItemOrcamento fonte) {
        if (fonte == FonteItemOrcamento.API_OBSERVADO) return TipoFontePreco.API_REAL;
        if (fonte == FonteItemOrcamento.HISTORICO) return TipoFontePreco.HISTORICO;
        return TipoFontePreco.DEMONSTRATIVO;
    }
}
