package br.com.locus.orcamento;

import br.com.locus.orcamento.dto.ItemOrcamentoComPrecoResposta;
import org.springframework.web.bind.annotation.*;
import br.com.locus.orcamento.dto.ItemOrcamentoResposta;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/itens-orcamento")
public class ItemOrcamentoController {

    private final ItemOrcamentoService itemOrcamentoService;

    public ItemOrcamentoController(
            ItemOrcamentoService itemOrcamentoService) {

        this.itemOrcamentoService = itemOrcamentoService;
    }

    @PostMapping("/viagem/{viagemId}")
    public ItemOrcamento salvar(
            @PathVariable Long viagemId,
            @RequestBody ItemOrcamento itemOrcamento,
            @RequestParam Long usuarioSolicitanteId) {

        return itemOrcamentoService.salvar(
                viagemId,
                itemOrcamento,
                usuarioSolicitanteId
        );
    }

    @DeleteMapping("/{itemId}")
    public void excluir(
            @PathVariable Long itemId,
            @RequestParam Long usuarioSolicitanteId) {

        itemOrcamentoService.excluir(itemId, usuarioSolicitanteId);
    }

    @PostMapping("/estimativa-rota/{estimativaRotaId}")
    public ItemOrcamento adicionarEstimativaRota(
            @PathVariable Long estimativaRotaId,
            @RequestParam Long usuarioSolicitanteId) {

        return itemOrcamentoService
                .adicionarEstimativaRota(
                        estimativaRotaId,
                        usuarioSolicitanteId
                );
    }

    @GetMapping("/viagem/{viagemId}")
    public List<ItemOrcamentoResposta> listarPorViagem(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        return itemOrcamentoService
                .listarPorViagem(
                        viagemId,
                        usuarioSolicitanteId
                )
                .stream()
                .map(ItemOrcamentoResposta::new)
                .toList();
    }

    @GetMapping("/viagem/{viagemId}/total")
    public BigDecimal calcularTotal(
            @PathVariable Long viagemId,
            @RequestParam Long usuarioSolicitanteId) {

        return itemOrcamentoService
                .calcularTotalViagem(
                        viagemId,
                        usuarioSolicitanteId
                );
    }

    @GetMapping("/viagem/{viagemId}/categoria/{categoria}/subtotal")
    public BigDecimal calcularSubtotal(
            @PathVariable Long viagemId,
            @PathVariable CategoriaOrcamento categoria,
            @RequestParam Long usuarioSolicitanteId) {

        return itemOrcamentoService
                .calcularSubtotalCategoria(
                        viagemId,
                        categoria,
                        usuarioSolicitanteId
                );
    }

    @GetMapping("/viagem/{viagemId}/categoria/{categoria}/saldo")
    public BigDecimal calcularSaldo(
            @PathVariable Long viagemId,
            @PathVariable CategoriaOrcamento categoria,
            @RequestParam Long usuarioSolicitanteId) {

        return itemOrcamentoService
                .calcularSaldoCategoria(
                        viagemId,
                        categoria,
                        usuarioSolicitanteId
                );
    }

    @GetMapping("/viagem/{viagemId}/categoria/{categoria}/excedido")
    public boolean verificarLimite(
            @PathVariable Long viagemId,
            @PathVariable CategoriaOrcamento categoria,
            @RequestParam Long usuarioSolicitanteId) {

        return itemOrcamentoService
                .categoriaUltrapassouLimite(
                        viagemId,
                        categoria,
                        usuarioSolicitanteId
                );
    }

    @GetMapping("/{itemId}/oportunidade-preco")
    public ItemOrcamentoComPrecoResposta consultarComPreco(
            @PathVariable Long itemId,
            @RequestParam Long usuarioSolicitanteId) {

        return itemOrcamentoService.consultarComPreco(
                itemId,
                usuarioSolicitanteId
        );
    }
}
