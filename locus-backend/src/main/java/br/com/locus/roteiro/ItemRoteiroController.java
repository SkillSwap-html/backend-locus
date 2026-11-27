package br.com.locus.roteiro;

import br.com.locus.compartilhamento.PermissaoCompartilhamentoService;
import br.com.locus.roteiro.dto.ItemRoteiroResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.locus.roteiro.dto.ItemRoteiroRequest;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/itens-roteiro")
public class ItemRoteiroController {

    private final ItemRoteiroService itemRoteiroService;
    private final DiaRoteiroRepository diaRoteiroRepository;
    private final PermissaoCompartilhamentoService permissaoCompartilhamentoService;

    public ItemRoteiroController(
            ItemRoteiroService itemRoteiroService,
            DiaRoteiroRepository diaRoteiroRepository,
            PermissaoCompartilhamentoService permissaoCompartilhamentoService) {

        this.itemRoteiroService = itemRoteiroService;
        this.diaRoteiroRepository = diaRoteiroRepository;
        this.permissaoCompartilhamentoService =
                permissaoCompartilhamentoService;
    }

    @PostMapping("/dia/{diaRoteiroId}/lugar/{lugarId}")
    public ItemRoteiroResposta cadastrarComLugar(
            @PathVariable Long diaRoteiroId,
            @PathVariable Long lugarId,
            @RequestParam Long usuarioId,
            @RequestBody ItemRoteiroRequest request) {

        Long viagemId = buscarViagemPorDia(diaRoteiroId);

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioId
        );

        ItemRoteiro itemRoteiro = new ItemRoteiro();

        itemRoteiro.setTitulo(request.getTitulo());
        itemRoteiro.setHorarioInicio(request.getHorarioInicio());
        itemRoteiro.setDuracaoMinutos(request.getDuracaoMinutos());
        itemRoteiro.setCustoEstimado(request.getCustoEstimado());
        itemRoteiro.setObservacoes(request.getObservacoes());
        itemRoteiro.setMotivoRecomendacao(request.getMotivoRecomendacao());

        ItemRoteiro itemSalvo = itemRoteiroService.salvar(
                diaRoteiroId,
                lugarId,
                itemRoteiro
        );

        return new ItemRoteiroResposta(itemSalvo);
    }

    @PostMapping("/dia/{diaRoteiroId}")
    public ItemRoteiroResposta cadastrarSemLugar(
            @PathVariable Long diaRoteiroId,
            @RequestParam Long usuarioId,
            @RequestBody ItemRoteiroRequest request) {

        Long viagemId = buscarViagemPorDia(diaRoteiroId);

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioId
        );

        ItemRoteiro itemRoteiro = new ItemRoteiro();

        itemRoteiro.setTitulo(request.getTitulo());
        itemRoteiro.setHorarioInicio(request.getHorarioInicio());
        itemRoteiro.setDuracaoMinutos(request.getDuracaoMinutos());
        itemRoteiro.setCustoEstimado(request.getCustoEstimado());
        itemRoteiro.setObservacoes(request.getObservacoes());
        itemRoteiro.setMotivoRecomendacao(request.getMotivoRecomendacao());

        ItemRoteiro itemSalvo = itemRoteiroService.salvar(
                diaRoteiroId,
                null,
                itemRoteiro
        );

        return new ItemRoteiroResposta(itemSalvo);
    }

    @PostMapping("/{itemId}/duplicar")
    public ItemRoteiroResposta duplicar(
            @PathVariable Long itemId,
            @RequestParam Long usuarioId,
            @RequestParam LocalTime novoHorario) {

        Long viagemId = buscarViagemPorItem(itemId);

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioId
        );

        ItemRoteiro itemDuplicado =
                itemRoteiroService.duplicar(
                        itemId,
                        novoHorario
                );

        return new ItemRoteiroResposta(itemDuplicado);
    }

    @PutMapping("/{itemId}")
    public ItemRoteiroResposta editar(
            @PathVariable Long itemId,
            @RequestParam Long usuarioId,
            @RequestParam(required = false) Long lugarId,
            @RequestParam Long versaoEsperada,
            @RequestBody ItemRoteiroRequest request) {

        Long viagemId = buscarViagemPorItem(itemId);

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioId
        );

        ItemRoteiro novosDados = new ItemRoteiro();

        novosDados.setTitulo(request.getTitulo());
        novosDados.setHorarioInicio(request.getHorarioInicio());
        novosDados.setDuracaoMinutos(request.getDuracaoMinutos());
        novosDados.setCustoEstimado(request.getCustoEstimado());
        novosDados.setObservacoes(request.getObservacoes());
        novosDados.setMotivoRecomendacao(request.getMotivoRecomendacao());

        ItemRoteiro itemAtualizado =
                itemRoteiroService.editar(
                        itemId,
                        lugarId,
                        versaoEsperada,
                        novosDados
                );

        return new ItemRoteiroResposta(itemAtualizado);
    }

    @PutMapping("/{itemId}/favorito")
    public ItemRoteiroResposta alterarFavorito(
            @PathVariable Long itemId,
            @RequestParam Long usuarioId,
            @RequestParam Boolean favorito,
            @RequestParam Long versaoEsperada) {

        Long viagemId = buscarViagemPorItem(itemId);

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioId
        );

        ItemRoteiro item =
                itemRoteiroService.alterarFavorito(
                        itemId,
                        favorito,
                        versaoEsperada
                );

        return new ItemRoteiroResposta(item);
    }

    @PutMapping("/dia/{diaRoteiroId}/reordenar")
    public List<ItemRoteiroResposta> reordenar(
            @PathVariable Long diaRoteiroId,
            @RequestParam Long usuarioId,
            @RequestBody List<ItemReordenacaoRequest> novaOrdem) {

        Long viagemId = buscarViagemPorDia(diaRoteiroId);

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioId
        );

        return itemRoteiroService
                .reordenar(diaRoteiroId, novaOrdem)
                .stream()
                .map(ItemRoteiroResposta::new)
                .toList();
    }

    @GetMapping("/dia/{diaRoteiroId}")
    public List<ItemRoteiroResposta> listarPorDia(
            @PathVariable Long diaRoteiroId,
            @RequestParam Long usuarioId) {

        Long viagemId = buscarViagemPorDia(diaRoteiroId);

        permissaoCompartilhamentoService.validarVisualizacao(
                viagemId,
                usuarioId
        );

        return itemRoteiroService
                .listarPorDia(diaRoteiroId)
                .stream()
                .map(ItemRoteiroResposta::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ItemRoteiroResposta buscarPorId(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        Long viagemId = buscarViagemPorItem(id);

        permissaoCompartilhamentoService.validarVisualizacao(
                viagemId,
                usuarioId
        );

        ItemRoteiro item =
                itemRoteiroService.buscarPorId(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item do roteiro não encontrado."
                                ));

        return new ItemRoteiroResposta(item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long itemId,
            @RequestParam Long usuarioId,
            @RequestParam Long versaoEsperada) {

        Long viagemId = buscarViagemPorItem(itemId);

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioId
        );

        itemRoteiroService.excluir(
                itemId,
                versaoEsperada
        );

        return ResponseEntity.noContent().build();
    }

    private Long buscarViagemPorDia(Long diaRoteiroId) {

        DiaRoteiro diaRoteiro =
                diaRoteiroRepository.findById(diaRoteiroId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dia do roteiro não encontrado."
                                )
                        );

        return diaRoteiro
                .getViagem()
                .getId();
    }

    private Long buscarViagemPorItem(Long itemId) {

        ItemRoteiro itemRoteiro =
                itemRoteiroService.buscarPorId(itemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item do roteiro não encontrado."
                                )
                        );

        return itemRoteiro
                .getDiaRoteiro()
                .getViagem()
                .getId();
    }
}