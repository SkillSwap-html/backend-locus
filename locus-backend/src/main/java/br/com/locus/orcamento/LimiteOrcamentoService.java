package br.com.locus.orcamento;

import br.com.locus.admin.ControleAcessoService;
import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.auditoria.ResultadoAuditoria;
import br.com.locus.orcamento.dto.AtualizarLimiteOrcamentoRequest;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class LimiteOrcamentoService {

    private final LimiteOrcamentoRepository limiteOrcamentoRepository;
    private final ViagemRepository viagemRepository;
    private final EventoAuditoriaService eventoAuditoriaService;
    private final ControleAcessoService controleAcessoService;

    public LimiteOrcamentoService(
            LimiteOrcamentoRepository limiteOrcamentoRepository,
            ViagemRepository viagemRepository,
            EventoAuditoriaService eventoAuditoriaService,
            ControleAcessoService controleAcessoService) {

        this.limiteOrcamentoRepository = limiteOrcamentoRepository;
        this.viagemRepository = viagemRepository;
        this.eventoAuditoriaService = eventoAuditoriaService;
        this.controleAcessoService = controleAcessoService;
    }

    public LimiteOrcamento salvar(
            Long viagemId,
            LimiteOrcamento limiteOrcamento) {

        Viagem viagem = viagemRepository.findById(viagemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Viagem não encontrada."
                        ));

        if (limiteOrcamento.getCategoria() == null) {
            throw new RuntimeException(
                    "A categoria do orçamento é obrigatória."
            );
        }

        if (limiteOrcamento.getValorLimite() == null
                || limiteOrcamento.getValorLimite()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "O valor do limite não pode ser negativo."
            );
        }

        if (limiteOrcamentoRepository
                .existsByViagemIdAndCategoria(
                        viagemId,
                        limiteOrcamento.getCategoria())) {

            throw new RuntimeException(
                    "Já existe um limite cadastrado para esta categoria."
            );
        }

        limiteOrcamento.setViagem(viagem);

        return limiteOrcamentoRepository.save(
                limiteOrcamento
        );
    }

    public LimiteOrcamento salvar(
            Long viagemId,
            Long usuarioSolicitanteId,
            LimiteOrcamento limiteOrcamento) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return salvar(
                viagemId,
                limiteOrcamento
        );
    }

    @Transactional
    public LimiteOrcamento atualizar(
            Long limiteId,
            AtualizarLimiteOrcamentoRequest request,
            Long usuarioSolicitanteId) {

        try {

            LimiteOrcamento limite =
                    limiteOrcamentoRepository.findById(limiteId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Limite de orçamento não encontrado."
                                    ));

            controleAcessoService.exigirProprietarioViagem(
                    limite.getViagem().getId(),
                    usuarioSolicitanteId
            );

            if (!request.isConfirmacaoUsuario()) {
                throw new RuntimeException(
                        "A alteração do limite exige confirmação explícita do usuário."
                );
            }

            if (request.getValorLimite() == null
                    || request.getValorLimite()
                    .compareTo(BigDecimal.ZERO) < 0) {

                throw new RuntimeException(
                        "O valor do limite não pode ser negativo."
                );
            }

            BigDecimal valorAnterior =
                    limite.getValorLimite();

            limite.setValorLimite(
                    request.getValorLimite()
            );

            LimiteOrcamento limiteSalvo =
                    limiteOrcamentoRepository.save(limite);

            eventoAuditoriaService.registrar(
                    usuarioSolicitanteId,
                    "ALTERAR_LIMITE_ORCAMENTO",
                    "LIMITE_ORCAMENTO",
                    limiteSalvo.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Limite da categoria "
                            + limiteSalvo.getCategoria()
                            + " alterado de "
                            + valorAnterior
                            + " para "
                            + limiteSalvo.getValorLimite()
                            + " com confirmação explícita do usuário."
            );

            return limiteSalvo;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioSolicitanteId,
                        "ALTERAR_LIMITE_ORCAMENTO",
                        "LIMITE_ORCAMENTO",
                        limiteId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
            }

            throw e;
        }
    }

    public List<LimiteOrcamento> listarPorViagem(
            Long viagemId) {

        return limiteOrcamentoRepository
                .findByViagemId(viagemId);
    }

    public List<LimiteOrcamento> listarPorViagem(
            Long viagemId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return listarPorViagem(viagemId);
    }

    public Optional<LimiteOrcamento> buscarPorCategoria(
            Long viagemId,
            CategoriaOrcamento categoria) {

        return limiteOrcamentoRepository
                .findByViagemIdAndCategoria(
                        viagemId,
                        categoria
                );
    }
}