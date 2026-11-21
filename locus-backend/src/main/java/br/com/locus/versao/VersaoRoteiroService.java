package br.com.locus.versao;

import br.com.locus.roteiro.DiaRoteiro;
import br.com.locus.roteiro.DiaRoteiroRepository;
import br.com.locus.roteiro.ItemRoteiro;
import br.com.locus.roteiro.ItemRoteiroRepository;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.locus.compartilhamento.PermissaoCompartilhamentoService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VersaoRoteiroService {

    private final VersaoRoteiroRepository versaoRoteiroRepository;
    private final ItemVersaoRoteiroRepository itemVersaoRoteiroRepository;
    private final ViagemRepository viagemRepository;
    private final DiaRoteiroRepository diaRoteiroRepository;
    private final ItemRoteiroRepository itemRoteiroRepository;
    private final PermissaoCompartilhamentoService permissaoCompartilhamentoService;

    public VersaoRoteiroService(
            VersaoRoteiroRepository versaoRoteiroRepository,
            ItemVersaoRoteiroRepository itemVersaoRoteiroRepository,
            ViagemRepository viagemRepository,
            DiaRoteiroRepository diaRoteiroRepository,
            ItemRoteiroRepository itemRoteiroRepository,
            PermissaoCompartilhamentoService permissaoCompartilhamentoService) {

        this.versaoRoteiroRepository = versaoRoteiroRepository;
        this.itemVersaoRoteiroRepository = itemVersaoRoteiroRepository;
        this.viagemRepository = viagemRepository;
        this.diaRoteiroRepository = diaRoteiroRepository;
        this.itemRoteiroRepository = itemRoteiroRepository;
        this.permissaoCompartilhamentoService = permissaoCompartilhamentoService;
    }

    @Transactional
    public VersaoRoteiro criarVersao(
            Long viagemId,
            String descricao) {

        Viagem viagem =
                viagemRepository.findById(viagemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Viagem não encontrada."
                                ));

        List<DiaRoteiro> dias =
                diaRoteiroRepository
                        .findByViagemIdOrderByDataAsc(
                                viagemId
                        );

        if (dias.isEmpty()) {
            throw new RuntimeException(
                    "A viagem ainda não possui roteiro para versionar."
            );
        }

        Integer numeroNovaVersao =
                calcularProximoNumeroVersao(
                        viagemId
                );

        VersaoRoteiro versao =
                new VersaoRoteiro();

        versao.setViagem(viagem);
        versao.setNumeroVersao(numeroNovaVersao);
        versao.setDescricao(descricao);
        versao.setDataCriacao(
                LocalDateTime.now()
        );

        VersaoRoteiro versaoSalva =
                versaoRoteiroRepository.save(
                        versao
                );

        for (DiaRoteiro dia : dias) {

            List<ItemRoteiro> itens =
                    itemRoteiroRepository
                            .findByDiaRoteiroIdOrderByOrdemAsc(
                                    dia.getId()
                            );

            for (ItemRoteiro item : itens) {

                ItemVersaoRoteiro itemVersao =
                        new ItemVersaoRoteiro();

                itemVersao.setVersaoRoteiro(
                        versaoSalva
                );

                itemVersao.setDataDia(
                        dia.getData()
                );

                itemVersao.setTitulo(
                        item.getTitulo()
                );

                if (item.getLugar() != null) {
                    itemVersao.setNomeLugar(
                            item.getLugar().getNome()
                    );
                }

                itemVersao.setHorarioInicio(
                        item.getHorarioInicio()
                );

                itemVersao.setDuracaoMinutos(
                        item.getDuracaoMinutos()
                );

                itemVersao.setOrdem(
                        item.getOrdem()
                );

                itemVersao.setCustoEstimado(
                        item.getCustoEstimado()
                );

                itemVersao.setObservacoes(
                        item.getObservacoes()
                );

                itemVersao.setMotivoRecomendacao(
                        item.getMotivoRecomendacao()
                );

                itemVersao.setFavorito(
                        item.getFavorito()
                );

                itemVersaoRoteiroRepository.save(
                        itemVersao
                );
            }
        }

        return versaoSalva;
    }

    public List<VersaoRoteiro> listarPorViagem(
            Long viagemId) {

        if (!viagemRepository.existsById(
                viagemId
        )) {

            throw new RuntimeException(
                    "Viagem não encontrada."
            );
        }

        return versaoRoteiroRepository
                .findByViagemIdOrderByNumeroVersaoDesc(
                        viagemId
                );
    }

    public List<ItemVersaoRoteiro> listarItensDaVersao(
            Long versaoId) {

        if (!versaoRoteiroRepository.existsById(
                versaoId
        )) {

            throw new RuntimeException(
                    "Versão do roteiro não encontrada."
            );
        }

        return itemVersaoRoteiroRepository
                .findByVersaoRoteiroIdOrderByDataDiaAscOrdemAsc(
                        versaoId
                );
    }

    public List<ComparacaoVersaoRoteiro> compararVersoes(
            Long versaoAntigaId,
            Long versaoNovaId) {

        VersaoRoteiro versaoAntiga =
                versaoRoteiroRepository.findById(versaoAntigaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Versão antiga não encontrada."
                                ));

        VersaoRoteiro versaoNova =
                versaoRoteiroRepository.findById(versaoNovaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Versão nova não encontrada."
                                ));

        if (!versaoAntiga.getViagem().getId()
                .equals(versaoNova.getViagem().getId())) {

            throw new RuntimeException(
                    "As versões devem pertencer à mesma viagem."
            );
        }

        List<ItemVersaoRoteiro> itensAntigos =
                itemVersaoRoteiroRepository
                        .findByVersaoRoteiroIdOrderByDataDiaAscOrdemAsc(
                                versaoAntigaId
                        );

        List<ItemVersaoRoteiro> itensNovos =
                itemVersaoRoteiroRepository
                        .findByVersaoRoteiroIdOrderByDataDiaAscOrdemAsc(
                                versaoNovaId
                        );

        List<ComparacaoVersaoRoteiro> diferencas =
                new ArrayList<>();

        for (ItemVersaoRoteiro itemAntigo : itensAntigos) {

            ItemVersaoRoteiro itemCorrespondente =
                    itensNovos.stream()
                            .filter(itemNovo ->
                                    mesmoLugarOuTitulo(
                                            itemAntigo,
                                            itemNovo
                                    ))
                            .findFirst()
                            .orElse(null);

            if (itemCorrespondente == null) {

                diferencas.add(
                        new ComparacaoVersaoRoteiro(
                                "REMOVIDO",
                                itemAntigo.getTitulo(),
                                "Item removido do roteiro."
                        )
                );

            } else {

                verificarAlteracoes(
                        itemAntigo,
                        itemCorrespondente,
                        diferencas
                );
            }
        }

        for (ItemVersaoRoteiro itemNovo : itensNovos) {

            boolean existiaAntes =
                    itensAntigos.stream()
                            .anyMatch(itemAntigo ->
                                    mesmoLugarOuTitulo(
                                            itemAntigo,
                                            itemNovo
                                    ));

            if (!existiaAntes) {

                diferencas.add(
                        new ComparacaoVersaoRoteiro(
                                "ADICIONADO",
                                itemNovo.getTitulo(),
                                "Item adicionado ao roteiro."
                        )
                );
            }
        }

        return diferencas;
    }

    @Transactional
    public VersaoRoteiro criarVersao(
            Long viagemId,
            String descricao,
            Long usuarioSolicitanteId) {

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioSolicitanteId
        );

        return criarVersao(
                viagemId,
                descricao
        );
    }

    public List<VersaoRoteiro> listarPorViagem(
            Long viagemId,
            Long usuarioSolicitanteId) {

        permissaoCompartilhamentoService.validarVisualizacao(
                viagemId,
                usuarioSolicitanteId
        );

        return listarPorViagem(viagemId);
    }

    public List<ItemVersaoRoteiro> listarItensDaVersao(
            Long versaoId,
            Long usuarioSolicitanteId) {

        VersaoRoteiro versao =
                versaoRoteiroRepository.findById(versaoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Versão do roteiro não encontrada."
                                )
                        );

        permissaoCompartilhamentoService.validarVisualizacao(
                versao.getViagem().getId(),
                usuarioSolicitanteId
        );

        return listarItensDaVersao(versaoId);
    }

    public List<ComparacaoVersaoRoteiro> compararVersoes(
            Long versaoAntigaId,
            Long versaoNovaId,
            Long usuarioSolicitanteId) {

        VersaoRoteiro versaoAntiga =
                versaoRoteiroRepository.findById(versaoAntigaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Versão antiga não encontrada."
                                )
                        );

        VersaoRoteiro versaoNova =
                versaoRoteiroRepository.findById(versaoNovaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Versão nova não encontrada."
                                )
                        );

        if (!versaoAntiga.getViagem().getId()
                .equals(versaoNova.getViagem().getId())) {

            throw new RuntimeException(
                    "As versões devem pertencer à mesma viagem."
            );
        }

        permissaoCompartilhamentoService.validarVisualizacao(
                versaoAntiga.getViagem().getId(),
                usuarioSolicitanteId
        );

        return compararVersoes(
                versaoAntigaId,
                versaoNovaId
        );
    }

    private Integer calcularProximoNumeroVersao(
            Long viagemId) {

        return versaoRoteiroRepository
                .findFirstByViagemIdOrderByNumeroVersaoDesc(
                        viagemId
                )
                .map(versao ->
                        versao.getNumeroVersao() + 1
                )
                .orElse(1);
    }

    private boolean mesmoLugarOuTitulo(
            ItemVersaoRoteiro item1,
            ItemVersaoRoteiro item2) {

        if (item1.getNomeLugar() != null
                && item2.getNomeLugar() != null) {

            return item1.getNomeLugar()
                    .equalsIgnoreCase(
                            item2.getNomeLugar()
                    );
        }

        return item1.getTitulo()
                .equalsIgnoreCase(
                        item2.getTitulo()
                );
    }

    private void verificarAlteracoes(
            ItemVersaoRoteiro antigo,
            ItemVersaoRoteiro novo,
            List<ComparacaoVersaoRoteiro> diferencas) {

        if (!antigo.getTitulo().equals(novo.getTitulo())) {

            diferencas.add(
                    new ComparacaoVersaoRoteiro(
                            "ALTERADO",
                            antigo.getTitulo(),
                            "Título alterado de '"
                                    + antigo.getTitulo()
                                    + "' para '"
                                    + novo.getTitulo()
                                    + "'."
                    )
            );
        }

        if (!java.util.Objects.equals(
                antigo.getHorarioInicio(),
                novo.getHorarioInicio())) {

            diferencas.add(
                    new ComparacaoVersaoRoteiro(
                            "ALTERADO",
                            novo.getTitulo(),
                            "Horário alterado."
                    )
            );
        }

        if (!java.util.Objects.equals(
                antigo.getDuracaoMinutos(),
                novo.getDuracaoMinutos())) {

            diferencas.add(
                    new ComparacaoVersaoRoteiro(
                            "ALTERADO",
                            novo.getTitulo(),
                            "Duração alterada."
                    )
            );
        }

        if (!java.util.Objects.equals(
                antigo.getCustoEstimado(),
                novo.getCustoEstimado())) {

            diferencas.add(
                    new ComparacaoVersaoRoteiro(
                            "ALTERADO",
                            novo.getTitulo(),
                            "Custo estimado alterado."
                    )
            );
        }

        if (!java.util.Objects.equals(
                antigo.getObservacoes(),
                novo.getObservacoes())) {

            diferencas.add(
                    new ComparacaoVersaoRoteiro(
                            "ALTERADO",
                            novo.getTitulo(),
                            "Observações alteradas."
                    )
            );
        }

        Boolean favoritoAntigo =
                antigo.getFavorito() != null
                        ? antigo.getFavorito()
                        : false;

        Boolean favoritoNovo =
                novo.getFavorito() != null
                        ? novo.getFavorito()
                        : false;

        if (!favoritoAntigo.equals(favoritoNovo)) {

            diferencas.add(
                    new ComparacaoVersaoRoteiro(
                            "ALTERADO",
                            novo.getTitulo(),
                            "Status de favorito alterado."
                    )
            );
        }
    }
}