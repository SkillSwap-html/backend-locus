package br.com.locus.orcamento;

import br.com.locus.orcamento.dto.ItemOrcamentoComPrecoResposta;
import br.com.locus.preco.dto.OportunidadePrecoResposta;
import br.com.locus.preco.SnapshotPrecoService;
import br.com.locus.transporte.EstimativaRota;
import br.com.locus.transporte.EstimativaRotaRepository;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.auditoria.ResultadoAuditoria;
import br.com.locus.admin.ControleAcessoService;
import br.com.locus.preco.dto.ResumoHistoricoPreco;
import br.com.locus.preco.SnapshotPreco;
import br.com.locus.preco.TipoFontePreco;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemOrcamentoService {

    private final ItemOrcamentoRepository itemOrcamentoRepository;
    private final LimiteOrcamentoRepository limiteOrcamentoRepository;
    private final ViagemRepository viagemRepository;
    private final EstimativaRotaRepository estimativaRotaRepository;
    private final SnapshotPrecoService snapshotPrecoService;
    private final ControleAcessoService controleAcessoService;
    private final EventoAuditoriaService eventoAuditoriaService;

    public ItemOrcamentoService(
            ItemOrcamentoRepository itemOrcamentoRepository,
            LimiteOrcamentoRepository limiteOrcamentoRepository,
            ViagemRepository viagemRepository,
            EstimativaRotaRepository estimativaRotaRepository,
            SnapshotPrecoService snapshotPrecoService,
            ControleAcessoService controleAcessoService,
            EventoAuditoriaService eventoAuditoriaService) {

        this.itemOrcamentoRepository = itemOrcamentoRepository;
        this.limiteOrcamentoRepository = limiteOrcamentoRepository;
        this.viagemRepository = viagemRepository;
        this.estimativaRotaRepository = estimativaRotaRepository;
        this.snapshotPrecoService = snapshotPrecoService;
        this.controleAcessoService = controleAcessoService;
        this.eventoAuditoriaService = eventoAuditoriaService;
    }

    @Transactional
    public ItemOrcamento salvar(
            Long viagemId,
            ItemOrcamento itemOrcamento,
            Long usuarioSolicitanteId) {

        try {

            Viagem viagem =
                    controleAcessoService.exigirProprietarioViagem(
                            viagemId,
                            usuarioSolicitanteId
                    );

            if (itemOrcamento.getCategoria() == null) {
                throw new RuntimeException(
                        "A categoria do item é obrigatória."
                );
            }

            if (itemOrcamento.getDescricao() == null
                    || itemOrcamento.getDescricao().isBlank()) {

                throw new RuntimeException(
                        "A descrição do item é obrigatória."
                );
            }

            if (itemOrcamento.getValor() == null
                    || itemOrcamento.getValor()
                    .compareTo(BigDecimal.ZERO) < 0) {

                throw new RuntimeException(
                        "O valor do item não pode ser negativo."
                );
            }

            if (viagem.getTipoOrcamento() == br.com.locus.perfil.TipoOrcamento.FIXO
                    && viagem.getValorOrcamentoTotal() != null) {
                BigDecimal totalAtual = itemOrcamentoRepository
                        .findByViagemId(viagemId)
                        .stream()
                        .map(ItemOrcamento::getValor)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (totalAtual.add(itemOrcamento.getValor())
                        .compareTo(viagem.getValorOrcamentoTotal()) > 0) {
                    throw new RuntimeException(
                            "Este item ultrapassa o orçamento total disponível da viagem."
                    );
                }
            }

            if (itemOrcamento.getTipoOferta() != null
                    && !itemOrcamento.getTipoOferta().isBlank()
                    && itemOrcamento.getOfertaId() == null) {

                throw new RuntimeException(
                        "O identificador da oferta é obrigatório quando o tipo da oferta é informado."
                );
            }

            if (itemOrcamento.getOfertaId() != null
                    && (itemOrcamento.getTipoOferta() == null
                    || itemOrcamento.getTipoOferta().isBlank())) {

                throw new RuntimeException(
                        "O tipo da oferta é obrigatório quando o identificador da oferta é informado."
                );
            }

            if (itemOrcamento.getTipoOferta() != null
                    && !itemOrcamento.getTipoOferta().isBlank()
                    && itemOrcamento.getOfertaId() != null) {

                boolean possuiHistorico =
                        snapshotPrecoService.possuiHistorico(
                                itemOrcamento.getTipoOferta(),
                                itemOrcamento.getOfertaId()
                        );

                if (!possuiHistorico) {
                    throw new RuntimeException(
                            "Não existem dados de preço para a oferta informada nos últimos 30 dias."
                    );
                }

                ResumoHistoricoPreco resumo =
                        snapshotPrecoService.calcularResumo(
                                itemOrcamento.getTipoOferta(),
                                itemOrcamento.getOfertaId()
                        );

                BigDecimal precoMaisRecente =
                        resumo.getPrecoMaisRecente();

                if (itemOrcamento.getValor()
                        .compareTo(precoMaisRecente) != 0) {

                    throw new RuntimeException(
                            "O valor do item deve corresponder ao preço mais recente da oferta."
                    );
                }
            }

            if (itemOrcamento.getTipoOferta() != null
                    && !itemOrcamento.getTipoOferta().isBlank()
                    && itemOrcamento.getOfertaId() != null) {

                SnapshotPreco snapshotMaisRecente =
                        snapshotPrecoService.buscarMaisRecente(
                                itemOrcamento.getTipoOferta(),
                                itemOrcamento.getOfertaId()
                        );

                if (snapshotMaisRecente.getFonte()
                        == TipoFontePreco.API_REAL) {

                    itemOrcamento.setFonte(
                            FonteItemOrcamento.API_OBSERVADO
                    );

                } else if (snapshotMaisRecente.getFonte()
                        == TipoFontePreco.HISTORICO) {

                    itemOrcamento.setFonte(
                            FonteItemOrcamento.HISTORICO
                    );

                } else {

                    itemOrcamento.setFonte(
                            FonteItemOrcamento.DEMONSTRATIVO
                    );
                }

            } else {

                itemOrcamento.setFonte(
                        FonteItemOrcamento.INFORMADO_USUARIO
                );
            }

            itemOrcamento.setViagem(viagem);

            ItemOrcamento itemSalvo =
                    itemOrcamentoRepository.save(itemOrcamento);

            String detalhes =
                    "Categoria " + itemSalvo.getCategoria()
                            + ", descrição " + itemSalvo.getDescricao()
                            + ", valor " + itemSalvo.getValor();

            if (itemSalvo.getTipoOferta() != null
                    && itemSalvo.getOfertaId() != null) {

                detalhes +=
                        ", oferta "
                                + itemSalvo.getTipoOferta()
                                + " "
                                + itemSalvo.getOfertaId();
            }

            eventoAuditoriaService.registrar(
                    usuarioSolicitanteId,
                    "ADICIONAR_ITEM_ORCAMENTO",
                    "ITEM_ORCAMENTO",
                    itemSalvo.getId(),
                    ResultadoAuditoria.SUCESSO,
                    detalhes
            );

            return itemSalvo;

        } catch (RuntimeException e) {

            eventoAuditoriaService.registrarFalha(
                    usuarioSolicitanteId,
                    "ADICIONAR_ITEM_ORCAMENTO",
                    "VIAGEM",
                    viagemId,
                    e.getMessage()
            );

            throw e;
        }
    }

    public ItemOrcamento adicionarEstimativaRota(
            Long estimativaRotaId,
            Long usuarioSolicitanteId) {

        EstimativaRota estimativaRota =
                estimativaRotaRepository.findById(estimativaRotaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Estimativa de rota não encontrada."
                                ));

        controleAcessoService.exigirProprietarioViagem(
                estimativaRota.getViagem().getId(),
                usuarioSolicitanteId
        );

        if (itemOrcamentoRepository
                .existsByEstimativaRotaId(estimativaRotaId)) {

            throw new RuntimeException(
                    "Esta estimativa de rota já foi adicionada ao orçamento."
            );
        }

        if (estimativaRota.getCustoTotalEstimado() == null) {
            throw new RuntimeException(
                    "A estimativa de rota não possui custo total calculado."
            );
        }

        ItemOrcamento itemOrcamento = new ItemOrcamento();

        itemOrcamento.setViagem(
                estimativaRota.getViagem()
        );

        itemOrcamento.setEstimativaRota(
                estimativaRota
        );

        itemOrcamento.setCategoria(
                CategoriaOrcamento.TRANSPORTE_LOCAL
        );

        itemOrcamento.setDescricao(
                "Estimativa de transporte - "
                        + estimativaRota.getModoTransporte()
        );

        itemOrcamento.setValor(
                estimativaRota.getCustoTotalEstimado()
        );

        itemOrcamento.setFonte(
                FonteItemOrcamento.ESTIMATIVA_CALCULADA
        );

        return itemOrcamentoRepository.save(
                itemOrcamento
        );
    }

    public List<ItemOrcamento> listarPorViagem(
            Long viagemId) {

        return itemOrcamentoRepository
                .findByViagemId(viagemId);
    }

    public List<ItemOrcamento> listarPorViagem(
            Long viagemId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return listarPorViagem(viagemId);
    }

    public BigDecimal calcularTotalViagem(
            Long viagemId) {

        return itemOrcamentoRepository
                .findByViagemId(viagemId)
                .stream()
                .map(ItemOrcamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalViagem(
            Long viagemId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return calcularTotalViagem(viagemId);
    }

    public BigDecimal calcularSubtotalCategoria(
            Long viagemId,
            CategoriaOrcamento categoria) {

        return itemOrcamentoRepository
                .findByViagemIdAndCategoria(
                        viagemId,
                        categoria
                )
                .stream()
                .map(ItemOrcamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularSubtotalCategoria(
            Long viagemId,
            CategoriaOrcamento categoria,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return calcularSubtotalCategoria(
                viagemId,
                categoria
        );
    }

    public BigDecimal calcularSaldoCategoria(
            Long viagemId,
            CategoriaOrcamento categoria) {

        LimiteOrcamento limite =
                limiteOrcamentoRepository
                        .findByViagemIdAndCategoria(
                                viagemId,
                                categoria
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Não existe limite cadastrado para esta categoria."
                                ));

        BigDecimal subtotal =
                calcularSubtotalCategoria(
                        viagemId,
                        categoria
                );

        return limite.getValorLimite()
                .subtract(subtotal);
    }

    public BigDecimal calcularSaldoCategoria(
            Long viagemId,
            CategoriaOrcamento categoria,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return calcularSaldoCategoria(
                viagemId,
                categoria
        );
    }

    public boolean categoriaUltrapassouLimite(
            Long viagemId,
            CategoriaOrcamento categoria) {

        BigDecimal saldo =
                calcularSaldoCategoria(
                        viagemId,
                        categoria
                );

        return saldo.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean categoriaUltrapassouLimite(
            Long viagemId,
            CategoriaOrcamento categoria,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return categoriaUltrapassouLimite(
                viagemId,
                categoria
        );
    }

    public ItemOrcamentoComPrecoResposta consultarComPreco(
            Long itemId,
            Long usuarioSolicitanteId) {

        ItemOrcamento item =
                itemOrcamentoRepository.findById(itemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item de orçamento não encontrado."
                                ));
        controleAcessoService.exigirProprietarioViagem(
                item.getViagem().getId(),
                usuarioSolicitanteId
        );

        if (item.getTipoOferta() == null
                || item.getTipoOferta().isBlank()
                || item.getOfertaId() == null) {

            throw new RuntimeException(
                    "Este item de orçamento não possui oferta de preço associada."
            );
        }

        OportunidadePrecoResposta oportunidade =
                snapshotPrecoService.analisarOportunidade(
                        item.getTipoOferta(),
                        item.getOfertaId(),
                        usuarioSolicitanteId
                );

        return new ItemOrcamentoComPrecoResposta(
                item.getId(),
                item.getCategoria(),
                item.getDescricao(),
                item.getValor(),
                item.getTipoOferta(),
                item.getOfertaId(),
                oportunidade.getClassificacao(),
                oportunidade.getMediaHistorica(),
                oportunidade.getJustificativa()
        );
    }
}
