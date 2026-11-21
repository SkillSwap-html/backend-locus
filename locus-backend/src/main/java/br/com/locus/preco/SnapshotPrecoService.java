package br.com.locus.preco;

import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.auditoria.ResultadoAuditoria;
import br.com.locus.preco.dto.OportunidadePrecoResposta;
import br.com.locus.preco.dto.ResumoHistoricoPreco;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SnapshotPrecoService {

    private final SnapshotPrecoRepository snapshotPrecoRepository;
    private final EventoAuditoriaService eventoAuditoriaService;

    public SnapshotPrecoService(
            SnapshotPrecoRepository snapshotPrecoRepository,
            EventoAuditoriaService eventoAuditoriaService) {

        this.snapshotPrecoRepository = snapshotPrecoRepository;
        this.eventoAuditoriaService = eventoAuditoriaService;
    }

    public boolean possuiHistorico(
            String tipoOferta,
            Long ofertaId) {

        return !listarUltimos30Dias(
                tipoOferta,
                ofertaId
        ).isEmpty();
    }

    @Transactional
    public SnapshotPreco registrar(
            SnapshotPreco snapshot,
            Long usuarioSolicitanteId) {

        try {

            if (snapshot.getTipoOferta() == null
                    || snapshot.getTipoOferta().isBlank()) {

                throw new RuntimeException(
                        "O tipo da oferta é obrigatório."
                );
            }

            if (snapshot.getOfertaId() == null) {
                throw new RuntimeException(
                        "O identificador da oferta é obrigatório."
                );
            }

            if (snapshot.getPreco() == null
                    || snapshot.getPreco()
                    .compareTo(BigDecimal.ZERO) <= 0) {

                throw new RuntimeException(
                        "O preço deve ser maior que zero."
                );
            }

            if (snapshot.getMoeda() == null
                    || snapshot.getMoeda().isBlank()) {

                throw new RuntimeException(
                        "A moeda é obrigatória."
                );
            }

            if (snapshot.getFonte() == null) {
                throw new RuntimeException(
                        "A fonte do preço é obrigatória."
                );
            }

            List<SnapshotPreco> historicoExistente =
                    listarUltimos30Dias(
                            snapshot.getTipoOferta(),
                            snapshot.getOfertaId()
                    );

            for (SnapshotPreco item : historicoExistente) {

                if (!item.getMoeda()
                        .equalsIgnoreCase(snapshot.getMoeda())) {

                    throw new RuntimeException(
                            "A moeda do snapshot deve ser a mesma utilizada no histórico da oferta."
                    );
                }
            }

            SnapshotPreco snapshotSalvo =
                    snapshotPrecoRepository.save(snapshot);

            eventoAuditoriaService.registrar(
                    usuarioSolicitanteId,
                    "REGISTRAR_SNAPSHOT_PRECO",
                    "SNAPSHOT_PRECO",
                    snapshotSalvo.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Snapshot registrado para "
                            + snapshotSalvo.getTipoOferta()
                            + " "
                            + snapshotSalvo.getOfertaId()
                            + ", preço "
                            + snapshotSalvo.getPreco()
                            + " "
                            + snapshotSalvo.getMoeda()
                            + ", fonte "
                            + snapshotSalvo.getFonte()
            );

            return snapshotSalvo;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioSolicitanteId,
                        "REGISTRAR_SNAPSHOT_PRECO",
                        "SNAPSHOT_PRECO",
                        null,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
            }

            throw e;
        }
    }

    public List<SnapshotPreco> listarUltimos30Dias(
            String tipoOferta,
            Long ofertaId) {

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataInicial = agora.minusDays(30);

        return snapshotPrecoRepository
                .findByTipoOfertaAndOfertaIdAndDataConsultaBetweenOrderByDataConsultaAsc(
                        tipoOferta,
                        ofertaId,
                        dataInicial,
                        agora
                );
    }

    public ResumoHistoricoPreco calcularResumo(
            String tipoOferta,
            Long ofertaId) {

        List<SnapshotPreco> snapshots =
                listarUltimos30Dias(tipoOferta, ofertaId);

        if (snapshots.isEmpty()) {
            throw new RuntimeException(
                    "Não existem dados de preço para esta oferta nos últimos 30 dias."
            );
        }

        List<BigDecimal> precos = new ArrayList<>();

        for (SnapshotPreco snapshot : snapshots) {
            precos.add(snapshot.getPreco());
        }

        precos.sort(Comparator.naturalOrder());

        BigDecimal menorPreco = precos.get(0);

        BigDecimal maiorPreco =
                precos.get(precos.size() - 1);

        BigDecimal soma = BigDecimal.ZERO;

        for (BigDecimal preco : precos) {
            soma = soma.add(preco);
        }

        BigDecimal mediaPreco =
                soma.divide(
                        BigDecimal.valueOf(precos.size()),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal medianaPreco;

        int tamanho = precos.size();

        if (tamanho % 2 == 0) {

            BigDecimal preco1 =
                    precos.get(tamanho / 2 - 1);

            BigDecimal preco2 =
                    precos.get(tamanho / 2);

            medianaPreco = preco1
                    .add(preco2)
                    .divide(
                            BigDecimal.valueOf(2),
                            2,
                            RoundingMode.HALF_UP
                    );

        } else {

            medianaPreco =
                    precos.get(tamanho / 2);
        }

        SnapshotPreco snapshotMaisAntigo =
                snapshots.get(0);

        SnapshotPreco snapshotMaisRecente =
                snapshots.get(snapshots.size() - 1);

        BigDecimal precoMaisRecente =
                snapshotMaisRecente.getPreco();

        BigDecimal variacaoPercentual =
                BigDecimal.ZERO;

        if (snapshotMaisAntigo.getPreco()
                .compareTo(BigDecimal.ZERO) != 0) {

            variacaoPercentual =
                    precoMaisRecente
                            .subtract(snapshotMaisAntigo.getPreco())
                            .divide(
                                    snapshotMaisAntigo.getPreco(),
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        long diasHistorico =
                ChronoUnit.DAYS.between(
                        snapshotMaisAntigo
                                .getDataConsulta()
                                .toLocalDate(),
                        snapshotMaisRecente
                                .getDataConsulta()
                                .toLocalDate()
                );

        boolean historicoSuficiente =
                diasHistorico >= 29;

        return new ResumoHistoricoPreco(
                menorPreco,
                maiorPreco,
                mediaPreco,
                medianaPreco,
                precoMaisRecente,
                variacaoPercentual,
                snapshots.size(),
                snapshotMaisAntigo.getDataConsulta(),
                snapshotMaisRecente.getDataConsulta(),
                historicoSuficiente
        );
    }

    public OportunidadePrecoResposta analisarOportunidade(
            String tipoOferta,
            Long ofertaId,
            Long usuarioSolicitanteId) {

        try {

            ResumoHistoricoPreco resumo =
                    calcularResumo(tipoOferta, ofertaId);

            BigDecimal precoAtual =
                    resumo.getPrecoMaisRecente();

            BigDecimal mediaHistorica =
                    resumo.getMediaPreco();

            ClassificacaoPreco classificacao;
            String justificativa;

            if (!resumo.isHistoricoSuficiente()) {

                classificacao =
                        ClassificacaoPreco.DADOS_INSUFICIENTES;

                justificativa =
                        "O histórico ainda não possui período suficiente para classificar o preço.";

            } else {

                BigDecimal limiteBom =
                        mediaHistorica
                                .multiply(new BigDecimal("0.90"))
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                );

                BigDecimal limiteAlto =
                        mediaHistorica
                                .multiply(new BigDecimal("1.10"))
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                );

                if (precoAtual.compareTo(limiteBom) <= 0) {

                    classificacao =
                            ClassificacaoPreco.BOM;

                    justificativa =
                            "O preço atual está pelo menos 10% abaixo da média histórica.";

                } else if (precoAtual.compareTo(limiteAlto) >= 0) {

                    classificacao =
                            ClassificacaoPreco.ALTO;

                    justificativa =
                            "O preço atual está pelo menos 10% acima da média histórica.";

                } else {

                    classificacao =
                            ClassificacaoPreco.NORMAL;

                    justificativa =
                            "O preço atual está dentro da faixa esperada em relação à média histórica.";
                }
            }

            OportunidadePrecoResposta resposta =
                    new OportunidadePrecoResposta(
                            precoAtual,
                            mediaHistorica,
                            classificacao,
                            justificativa
                    );

            eventoAuditoriaService.registrar(
                    usuarioSolicitanteId,
                    "ANALISAR_OPORTUNIDADE_PRECO",
                    "OFERTA",
                    ofertaId,
                    ResultadoAuditoria.SUCESSO,
                    "Tipo "
                            + tipoOferta
                            + ", preço atual "
                            + precoAtual
                            + ", média histórica "
                            + mediaHistorica
                            + ", regra de comparação 10%, resultado "
                            + classificacao
            );

            return resposta;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioSolicitanteId,
                        "ANALISAR_OPORTUNIDADE_PRECO",
                        "OFERTA",
                        ofertaId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
            }

            throw e;
        }
    }

    public SnapshotPreco buscarMaisRecente(
            String tipoOferta,
            Long ofertaId) {

        List<SnapshotPreco> snapshots =
                listarUltimos30Dias(
                        tipoOferta,
                        ofertaId
                );

        if (snapshots.isEmpty()) {
            throw new RuntimeException(
                    "Não existem dados de preço para esta oferta nos últimos 30 dias."
            );
        }

        return snapshots.get(
                snapshots.size() - 1
        );
    }
}