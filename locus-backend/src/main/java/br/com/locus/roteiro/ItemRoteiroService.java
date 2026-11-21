package br.com.locus.roteiro;

import br.com.locus.lugar.Lugar;
import br.com.locus.lugar.LugarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.locus.exception.ConflitoEdicaoException;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItemRoteiroService {

    private final ItemRoteiroRepository itemRoteiroRepository;
    private final DiaRoteiroRepository diaRoteiroRepository;
    private final LugarRepository lugarRepository;
    private final DeslocamentoRoteiroRepository deslocamentoRoteiroRepository;

    public ItemRoteiroService(
            ItemRoteiroRepository itemRoteiroRepository,
            DiaRoteiroRepository diaRoteiroRepository,
            LugarRepository lugarRepository,
            DeslocamentoRoteiroRepository deslocamentoRoteiroRepository) {

        this.itemRoteiroRepository = itemRoteiroRepository;
        this.diaRoteiroRepository = diaRoteiroRepository;
        this.lugarRepository = lugarRepository;
        this.deslocamentoRoteiroRepository = deslocamentoRoteiroRepository;
    }

    public ItemRoteiro salvar(
            Long diaRoteiroId,
            Long lugarId,
            ItemRoteiro itemRoteiro) {

        DiaRoteiro diaRoteiro =
                diaRoteiroRepository.findById(diaRoteiroId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dia do roteiro não encontrado."
                                ));

        validarItemSemOrdem(itemRoteiro);

        itemRoteiro.setDiaRoteiro(diaRoteiro);

        if (lugarId != null) {

            Lugar lugar =
                    lugarRepository.findById(lugarId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Lugar não encontrado."
                                    ));

            itemRoteiro.setLugar(lugar);
        }

        Integer novaOrdem =
                calcularProximaOrdem(diaRoteiroId);

        itemRoteiro.setOrdem(novaOrdem);

        validarConflitoHorario(
                diaRoteiroId,
                itemRoteiro,
                null
        );

        return itemRoteiroRepository.save(
                itemRoteiro
        );
    }

    public ItemRoteiro editar(
            Long itemId,
            Long lugarId,
            Long versaoEsperada,
            ItemRoteiro novosDados) {

        ItemRoteiro itemExistente =
                itemRoteiroRepository.findById(itemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item do roteiro não encontrado."
                                ));

        if (versaoEsperada == null) {
            throw new RuntimeException(
                    "A versão do item é obrigatória para realizar a edição."
            );
        }

        if (!itemExistente.getVersao().equals(versaoEsperada)) {
            throw new ConflitoEdicaoException(
                    "Conflito de edição: este item foi alterado por outro usuário. Atualize os dados antes de editar novamente."
            );
        }

        validarItemSemOrdem(novosDados);

        if (lugarId != null) {

            Lugar lugar =
                    lugarRepository.findById(lugarId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Lugar não encontrado."
                                    ));

            itemExistente.setLugar(lugar);

        } else {

            itemExistente.setLugar(null);
        }

        itemExistente.setTitulo(
                novosDados.getTitulo()
        );

        itemExistente.setHorarioInicio(
                novosDados.getHorarioInicio()
        );

        itemExistente.setDuracaoMinutos(
                novosDados.getDuracaoMinutos()
        );

        itemExistente.setCustoEstimado(
                novosDados.getCustoEstimado()
        );

        itemExistente.setObservacoes(
                novosDados.getObservacoes()
        );

        itemExistente.setMotivoRecomendacao(
                novosDados.getMotivoRecomendacao()
        );

        Long diaRoteiroId =
                itemExistente
                        .getDiaRoteiro()
                        .getId();

        validarConflitoHorario(
                diaRoteiroId,
                itemExistente,
                itemExistente.getId()
        );

        return itemRoteiroRepository.save(
                itemExistente
        );
    }

    public ItemRoteiro duplicar(
            Long itemId,
            LocalTime novoHorario) {

        ItemRoteiro itemOriginal =
                itemRoteiroRepository.findById(itemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item do roteiro não encontrado."
                                ));

        if (novoHorario == null) {

            throw new RuntimeException(
                    "O novo horário é obrigatório."
            );
        }

        Long diaRoteiroId =
                itemOriginal
                        .getDiaRoteiro()
                        .getId();

        ItemRoteiro novoItem =
                new ItemRoteiro();

        novoItem.setDiaRoteiro(
                itemOriginal.getDiaRoteiro()
        );

        novoItem.setLugar(
                itemOriginal.getLugar()
        );

        novoItem.setTitulo(
                itemOriginal.getTitulo()
        );

        novoItem.setHorarioInicio(
                novoHorario
        );

        novoItem.setDuracaoMinutos(
                itemOriginal.getDuracaoMinutos()
        );

        novoItem.setOrdem(
                calcularProximaOrdem(diaRoteiroId)
        );

        novoItem.setCustoEstimado(
                itemOriginal.getCustoEstimado()
        );

        novoItem.setObservacoes(
                itemOriginal.getObservacoes()
        );

        novoItem.setMotivoRecomendacao(
                itemOriginal.getMotivoRecomendacao()
        );

        validarConflitoHorario(
                diaRoteiroId,
                novoItem,
                null
        );

        return itemRoteiroRepository.save(
                novoItem
        );
    }

    @Transactional
    public List<ItemRoteiro> reordenar(
            Long diaRoteiroId,
            List<ItemReordenacaoRequest> itensReordenados) {

        DiaRoteiro diaRoteiro =
                diaRoteiroRepository.findById(diaRoteiroId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dia do roteiro não encontrado."
                                ));

        if (itensReordenados == null || itensReordenados.isEmpty()) {
            throw new RuntimeException(
                    "Informe os itens na nova ordem."
            );
        }

        List<ItemRoteiro> itensDoDia =
                itemRoteiroRepository
                        .findByDiaRoteiroIdOrderByOrdemAsc(
                                diaRoteiro.getId()
                        );

        if (itensReordenados.size() != itensDoDia.size()) {
            throw new RuntimeException(
                    "Todos os itens do dia devem ser informados na reordenação."
            );
        }

        long quantidadeIdsDiferentes =
                itensReordenados.stream()
                        .map(ItemReordenacaoRequest::getItemId)
                        .distinct()
                        .count();

        if (quantidadeIdsDiferentes != itensReordenados.size()) {
            throw new RuntimeException(
                    "Não é permitido repetir itens na reordenação."
            );
        }

        for (ItemReordenacaoRequest itemRequest : itensReordenados) {

            if (itemRequest.getItemId() == null) {
                throw new RuntimeException(
                        "O ID do item é obrigatório."
                );
            }

            if (itemRequest.getVersaoEsperada() == null) {
                throw new RuntimeException(
                        "A versão de todos os itens é obrigatória para realizar a reordenação."
                );
            }

            ItemRoteiro itemExistente =
                    itensDoDia.stream()
                            .filter(item ->
                                    item.getId().equals(
                                            itemRequest.getItemId()
                                    )
                            )
                            .findFirst()
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Um dos itens informados não pertence a este dia do roteiro."
                                    ));

            if (!itemExistente.getVersao()
                    .equals(itemRequest.getVersaoEsperada())) {

                throw new ConflitoEdicaoException(
                        "Conflito de edição: um dos itens do roteiro foi alterado por outro usuário. Atualize os dados antes de reordenar novamente."
                );
            }
        }

        for (int i = 0; i < itensReordenados.size(); i++) {

            Long itemId =
                    itensReordenados
                            .get(i)
                            .getItemId();

            ItemRoteiro item =
                    itensDoDia.stream()
                            .filter(itemRoteiro ->
                                    itemRoteiro
                                            .getId()
                                            .equals(itemId)
                            )
                            .findFirst()
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Item do roteiro não encontrado."
                                    ));

            item.setOrdem(i + 1);
        }

        itemRoteiroRepository.saveAll(itensDoDia);

        return itemRoteiroRepository
                .findByDiaRoteiroIdOrderByOrdemAsc(
                        diaRoteiroId
                );
    }

    @Transactional
    public void excluir(
            Long itemId,
            Long versaoEsperada) {

        ItemRoteiro item =
                itemRoteiroRepository.findById(itemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item do roteiro não encontrado."
                                ));

        if (versaoEsperada == null) {
            throw new RuntimeException(
                    "A versão do item é obrigatória para realizar a exclusão."
            );
        }

        if (!item.getVersao().equals(versaoEsperada)) {
            throw new ConflitoEdicaoException(
                    "Conflito de edição: este item foi alterado por outro usuário. Atualize os dados antes de excluir novamente."
            );
        }

        Long diaRoteiroId =
                item.getDiaRoteiro().getId();

        List<DeslocamentoRoteiro> deslocamentosRelacionados =
                deslocamentoRoteiroRepository
                        .findByItemOrigemIdOrItemDestinoId(
                                itemId,
                                itemId
                        );

        if (!deslocamentosRelacionados.isEmpty()) {

            deslocamentoRoteiroRepository
                    .deleteAll(deslocamentosRelacionados);

            deslocamentoRoteiroRepository.flush();
        }

        itemRoteiroRepository.delete(item);
        itemRoteiroRepository.flush();

        List<ItemRoteiro> itensRestantes =
                itemRoteiroRepository
                        .findByDiaRoteiroIdOrderByOrdemAsc(
                                diaRoteiroId
                        );

        for (int i = 0; i < itensRestantes.size(); i++) {
            itensRestantes.get(i).setOrdem(i + 1);
        }

        itemRoteiroRepository.saveAll(itensRestantes);
    }

    public List<ItemRoteiro> listarPorDia(
            Long diaRoteiroId) {

        if (!diaRoteiroRepository.existsById(diaRoteiroId)) {

            throw new RuntimeException(
                    "Dia do roteiro não encontrado."
            );
        }

        return itemRoteiroRepository
                .findByDiaRoteiroIdOrderByOrdemAsc(
                        diaRoteiroId
                );
    }

    public Optional<ItemRoteiro> buscarPorId(
            Long id) {

        return itemRoteiroRepository.findById(id);
    }

    public ItemRoteiro alterarFavorito(
            Long itemId,
            Boolean favorito,
            Long versaoEsperada) {

        ItemRoteiro item =
                itemRoteiroRepository.findById(itemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item do roteiro não encontrado."
                                ));

        if (versaoEsperada == null) {
            throw new RuntimeException(
                    "A versão do item é obrigatória para realizar a alteração."
            );
        }

        if (!item.getVersao().equals(versaoEsperada)) {
            throw new ConflitoEdicaoException(
                    "Conflito de edição: este item foi alterado por outro usuário. Atualize os dados antes de editar novamente."
            );
        }

        if (favorito == null) {
            throw new RuntimeException(
                    "Informe se o item deve ser favorito ou não."
            );
        }

        item.setFavorito(favorito);

        return itemRoteiroRepository.save(item);
    }

    private Integer calcularProximaOrdem(
            Long diaRoteiroId) {

        Optional<ItemRoteiro> ultimoItem =
                itemRoteiroRepository
                        .findFirstByDiaRoteiroIdOrderByOrdemDesc(
                                diaRoteiroId
                        );

        if (ultimoItem.isEmpty()) {
            return 1;
        }

        return ultimoItem
                .get()
                .getOrdem() + 1;
    }

    private void validarItemSemOrdem(
            ItemRoteiro itemRoteiro) {

        if (itemRoteiro.getTitulo() == null
                || itemRoteiro.getTitulo().isBlank()) {

            throw new RuntimeException(
                    "O título da atividade é obrigatório."
            );
        }

        if (itemRoteiro.getHorarioInicio() == null) {

            throw new RuntimeException(
                    "O horário de início é obrigatório."
            );
        }

        if (itemRoteiro.getDuracaoMinutos() == null
                || itemRoteiro.getDuracaoMinutos() <= 0) {

            throw new RuntimeException(
                    "A duração da atividade deve ser maior que zero."
            );
        }

        if (itemRoteiro.getCustoEstimado() != null
                && itemRoteiro.getCustoEstimado() < 0) {

            throw new RuntimeException(
                    "O custo estimado não pode ser negativo."
            );
        }
    }

    private void validarConflitoHorario(
            Long diaRoteiroId,
            ItemRoteiro itemRoteiro,
            Long itemIgnoradoId) {

        List<ItemRoteiro> itensExistentes =
                itemRoteiroRepository
                        .findByDiaRoteiroIdOrderByOrdemAsc(
                                diaRoteiroId
                        );

        for (ItemRoteiro itemExistente : itensExistentes) {

            if (itemIgnoradoId != null
                    && itemExistente.getId()
                    .equals(itemIgnoradoId)) {

                continue;
            }

            int inicioExistente =
                    itemExistente.getHorarioInicio().toSecondOfDay() / 60;

            int fimExistente =
                    inicioExistente + itemExistente.getDuracaoMinutos();

            int novoInicio =
                    itemRoteiro.getHorarioInicio().toSecondOfDay() / 60;

            int novoFim =
                    novoInicio + itemRoteiro.getDuracaoMinutos();

            boolean existeConflito =
                    novoInicio < fimExistente
                            && novoFim > inicioExistente;

            if (existeConflito) {

                LocalTime fimFormatado =
                        itemExistente.getHorarioInicio()
                                .plusMinutes(itemExistente.getDuracaoMinutos());

                throw new RuntimeException(
                        String.format(
                                "A atividade possui conflito de horário com '%s' (%s às %s).",
                                itemExistente.getTitulo(),
                                itemExistente.getHorarioInicio(),
                                fimFormatado
                        )
                );
            }
        }
    }
}