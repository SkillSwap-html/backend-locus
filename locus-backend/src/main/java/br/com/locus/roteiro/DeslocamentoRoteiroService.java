package br.com.locus.roteiro;

import br.com.locus.compartilhamento.PermissaoCompartilhamentoService;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

@Service
public class DeslocamentoRoteiroService {

    private final DeslocamentoRoteiroRepository deslocamentoRepository;
    private final ItemRoteiroRepository itemRoteiroRepository;
    private final PermissaoCompartilhamentoService permissaoCompartilhamentoService;

    public DeslocamentoRoteiroService(
            DeslocamentoRoteiroRepository deslocamentoRepository,
            ItemRoteiroRepository itemRoteiroRepository,
            PermissaoCompartilhamentoService permissaoCompartilhamentoService) {

        this.deslocamentoRepository = deslocamentoRepository;
        this.itemRoteiroRepository = itemRoteiroRepository;
        this.permissaoCompartilhamentoService = permissaoCompartilhamentoService;
    }

    public DeslocamentoRoteiro salvar(
            Long itemOrigemId,
            Long itemDestinoId,
            DeslocamentoRoteiro deslocamento) {

        ItemRoteiro itemOrigem = itemRoteiroRepository
                .findById(itemOrigemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item de origem não encontrado."
                        ));

        ItemRoteiro itemDestino = itemRoteiroRepository
                .findById(itemDestinoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item de destino não encontrado."
                        ));

        if (itemOrigemId.equals(itemDestinoId)) {
            throw new RuntimeException(
                    "O item de origem e o item de destino não podem ser iguais."
            );
        }

        if (!itemOrigem.getDiaRoteiro().getId()
                .equals(itemDestino.getDiaRoteiro().getId())) {

            throw new RuntimeException(
                    "Os itens do deslocamento devem pertencer ao mesmo dia do roteiro."
            );
        }

        if (deslocamentoRepository
                .existsByItemOrigemIdAndItemDestinoId(
                        itemOrigemId,
                        itemDestinoId)) {

            throw new RuntimeException(
                    "Já existe um deslocamento entre estes itens."
            );
        }

        if (deslocamento.getDuracaoMinutos() == null
                || deslocamento.getDuracaoMinutos() < 0) {

            throw new RuntimeException(
                    "A duração do deslocamento deve ser informada e não pode ser negativa."
            );
        }

        if (deslocamento.getMargemMinutos() == null
                || deslocamento.getMargemMinutos() < 0) {

            throw new RuntimeException(
                    "A margem de tempo deve ser informada e não pode ser negativa."
            );
        }

        if (deslocamento.getFonteEstimativa() == null) {
            throw new RuntimeException(
                    "A fonte da estimativa é obrigatória."
            );
        }

        LocalTime fimAtividadeOrigem =
                itemOrigem.getHorarioInicio()
                        .plusMinutes(
                                itemOrigem.getDuracaoMinutos()
                        );

        LocalTime horarioMinimoDestino =
                fimAtividadeOrigem
                        .plusMinutes(
                                deslocamento.getDuracaoMinutos()
                        )
                        .plusMinutes(
                                deslocamento.getMargemMinutos()
                        );

        if (itemDestino.getHorarioInicio()
                .isBefore(horarioMinimoDestino)) {

            throw new RuntimeException(
                    "Não há tempo suficiente entre as atividades para o deslocamento e a margem de segurança."
            );
        }

        deslocamento.setItemOrigem(itemOrigem);
        deslocamento.setItemDestino(itemDestino);

        return deslocamentoRepository.save(deslocamento);
    }

    public DeslocamentoRoteiro salvar(
            Long itemOrigemId,
            Long itemDestinoId,
            Long usuarioSolicitanteId,
            DeslocamentoRoteiro deslocamento) {

        ItemRoteiro itemOrigem =
                buscarItem(itemOrigemId);

        Long viagemId =
                itemOrigem
                        .getDiaRoteiro()
                        .getViagem()
                        .getId();

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioSolicitanteId
        );

        return salvar(
                itemOrigemId,
                itemDestinoId,
                deslocamento
        );
    }

    public Optional<DeslocamentoRoteiro> buscarEntreItens(
            Long itemOrigemId,
            Long itemDestinoId) {

        return deslocamentoRepository
                .findByItemOrigemIdAndItemDestinoId(
                        itemOrigemId,
                        itemDestinoId
                );
    }

    public Optional<DeslocamentoRoteiro> buscarEntreItens(
            Long itemOrigemId,
            Long itemDestinoId,
            Long usuarioSolicitanteId) {

        ItemRoteiro itemOrigem =
                buscarItem(itemOrigemId);

        Long viagemId =
                itemOrigem
                        .getDiaRoteiro()
                        .getViagem()
                        .getId();

        permissaoCompartilhamentoService.validarVisualizacao(
                viagemId,
                usuarioSolicitanteId
        );

        return buscarEntreItens(
                itemOrigemId,
                itemDestinoId
        );
    }

    private ItemRoteiro buscarItem(Long itemId) {

        return itemRoteiroRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item do roteiro não encontrado."
                        )
                );
    }
}