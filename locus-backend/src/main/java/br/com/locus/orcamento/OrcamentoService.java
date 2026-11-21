package br.com.locus.orcamento;

import br.com.locus.orcamento.dto.ResumoCategoriaOrcamento;
import br.com.locus.orcamento.dto.ResumoOrcamento;
import br.com.locus.perfil.PerfilViajante;
import br.com.locus.perfil.PerfilViajanteRepository;
import br.com.locus.perfil.PreferenciaViagem;
import br.com.locus.perfil.PreferenciaViagemRepository;
import br.com.locus.perfil.TipoOrcamento;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import org.springframework.stereotype.Service;
import br.com.locus.admin.ControleAcessoService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrcamentoService {

    private final ViagemRepository viagemRepository;
    private final PerfilViajanteRepository perfilViajanteRepository;
    private final PreferenciaViagemRepository preferenciaViagemRepository;
    private final ItemOrcamentoRepository itemOrcamentoRepository;
    private final LimiteOrcamentoRepository limiteOrcamentoRepository;
    private final ControleAcessoService controleAcessoService;

    public OrcamentoService(
            ViagemRepository viagemRepository,
            PerfilViajanteRepository perfilViajanteRepository,
            PreferenciaViagemRepository preferenciaViagemRepository,
            ItemOrcamentoRepository itemOrcamentoRepository,
            LimiteOrcamentoRepository limiteOrcamentoRepository,
            ControleAcessoService controleAcessoService) {

        this.viagemRepository = viagemRepository;
        this.perfilViajanteRepository = perfilViajanteRepository;
        this.preferenciaViagemRepository = preferenciaViagemRepository;
        this.itemOrcamentoRepository = itemOrcamentoRepository;
        this.limiteOrcamentoRepository = limiteOrcamentoRepository;
        this.controleAcessoService = controleAcessoService;
    }

    public ResumoOrcamento gerarResumo(Long viagemId) {

        Viagem viagem = viagemRepository.findById(viagemId)
                .orElseThrow(() ->
                        new RuntimeException("Viagem não encontrada."));

        PerfilViajante perfilViajante = perfilViajanteRepository
                .findByUsuarioId(viagem.getUsuario().getId())
                .orElseThrow(() ->
                        new RuntimeException("Perfil de viajante não encontrado."));

        PreferenciaViagem preferenciaViagem = preferenciaViagemRepository
                .findByPerfilViajanteId(perfilViajante.getId())
                .orElseThrow(() ->
                        new RuntimeException("Preferências de viagem não encontradas."));

        List<ItemOrcamento> itens =
                itemOrcamentoRepository.findByViagemId(viagemId);

        BigDecimal totalGasto = itens.stream()
                .map(ItemOrcamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ResumoOrcamento resumo = new ResumoOrcamento();

        resumo.setTipoOrcamento(
                preferenciaViagem.getTipoOrcamento()
        );

        resumo.setTotalGasto(totalGasto);

        if (preferenciaViagem.getTipoOrcamento() == TipoOrcamento.FIXO) {

            BigDecimal limiteTotal =
                    preferenciaViagem.getValorOrcamento();

            BigDecimal saldoTotal =
                    limiteTotal.subtract(totalGasto);

            BigDecimal excessoTotal = BigDecimal.ZERO;

            if (saldoTotal.compareTo(BigDecimal.ZERO) < 0) {
                excessoTotal = saldoTotal.abs();
            }

            resumo.setLimiteTotal(limiteTotal);
            resumo.setSaldoTotal(saldoTotal);
            resumo.setExcessoTotal(excessoTotal);

        } else {

            resumo.setLimiteTotal(null);
            resumo.setSaldoTotal(null);
            resumo.setExcessoTotal(BigDecimal.ZERO);
        }

        resumo.setCategorias(
                gerarResumoCategorias(viagemId)
        );

        return resumo;
    }

    public ResumoOrcamento gerarResumo(
            Long viagemId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return gerarResumo(viagemId);
    }

    private List<ResumoCategoriaOrcamento> gerarResumoCategorias(
            Long viagemId) {

        List<ResumoCategoriaOrcamento> resumos =
                new ArrayList<>();

        for (CategoriaOrcamento categoria :
                CategoriaOrcamento.values()) {

            BigDecimal subtotal =
                    itemOrcamentoRepository
                            .findByViagemIdAndCategoria(
                                    viagemId,
                                    categoria
                            )
                            .stream()
                            .map(ItemOrcamento::getValor)
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );

            LimiteOrcamento limite =
                    limiteOrcamentoRepository
                            .findByViagemIdAndCategoria(
                                    viagemId,
                                    categoria
                            )
                            .orElse(null);

            BigDecimal valorLimite = null;
            BigDecimal saldo = null;
            BigDecimal excesso = BigDecimal.ZERO;

            if (limite != null) {

                valorLimite = limite.getValorLimite();

                saldo = valorLimite.subtract(subtotal);

                if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                    excesso = saldo.abs();
                }
            }

            ResumoCategoriaOrcamento resumoCategoria =
                    new ResumoCategoriaOrcamento(
                            categoria,
                            subtotal,
                            valorLimite,
                            saldo,
                            excesso
                    );

            resumos.add(resumoCategoria);
        }

        return resumos;
    }
}