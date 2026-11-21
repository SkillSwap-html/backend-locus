package br.com.locus.compartilhamento;

import br.com.locus.compartilhamento.dto.DiaRoteiroCompartilhadoResposta;
import br.com.locus.compartilhamento.dto.ItemRoteiroCompartilhadoResposta;
import br.com.locus.exception.AcessoNegadoException;
import br.com.locus.orcamento.OrcamentoService;
import br.com.locus.orcamento.dto.ResumoOrcamento;
import br.com.locus.roteiro.DiaRoteiro;
import br.com.locus.roteiro.DiaRoteiroRepository;
import br.com.locus.roteiro.ItemRoteiroRepository;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.locus.compartilhamento.dto.EstimativaRotaCompartilhadaResposta;
import br.com.locus.transporte.EstimativaRota;
import br.com.locus.transporte.EstimativaRotaRepository;
import br.com.locus.admin.ControleAcessoService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LinkCompartilhamentoService {

    private final LinkCompartilhamentoRepository linkRepository;
    private final ViagemRepository viagemRepository;
    private final DiaRoteiroRepository diaRoteiroRepository;
    private final ItemRoteiroRepository itemRoteiroRepository;
    private final OrcamentoService orcamentoService;
    private final EstimativaRotaRepository estimativaRotaRepository;
    private final ControleAcessoService controleAcessoService;

    public LinkCompartilhamentoService(
            LinkCompartilhamentoRepository linkRepository,
            ViagemRepository viagemRepository,
            DiaRoteiroRepository diaRoteiroRepository,
            ItemRoteiroRepository itemRoteiroRepository,
            OrcamentoService orcamentoService,
            EstimativaRotaRepository estimativaRotaRepository,
            ControleAcessoService controleAcessoService) {

        this.linkRepository = linkRepository;
        this.viagemRepository = viagemRepository;
        this.diaRoteiroRepository = diaRoteiroRepository;
        this.itemRoteiroRepository = itemRoteiroRepository;
        this.orcamentoService = orcamentoService;
        this.estimativaRotaRepository = estimativaRotaRepository;
        this.controleAcessoService = controleAcessoService;
    }

    @Transactional
    public LinkCompartilhamento criarLink(
            Long viagemId,
            Long usuarioSolicitanteId) {

        Viagem viagem = buscarViagem(viagemId);

        validarProprietario(
                viagem,
                usuarioSolicitanteId
        );

        boolean possuiLinkAtivo =
                linkRepository
                        .findFirstByViagemIdAndAtivoTrue(viagemId)
                        .isPresent();

        if (possuiLinkAtivo) {
            throw new RuntimeException(
                    "Esta viagem já possui um link de compartilhamento ativo."
            );
        }

        LinkCompartilhamento link = new LinkCompartilhamento();

        link.setViagem(viagem);
        link.setToken(gerarToken());
        link.setAtivo(true);
        link.setDataCriacao(LocalDateTime.now());
        link.setDataRevogacao(null);

        return linkRepository.save(link);
    }

    public LinkCompartilhamento buscarPorToken(String token) {

        if (token == null || token.isBlank()) {
            throw new RuntimeException(
                    "O token do compartilhamento deve ser informado."
            );
        }

        return linkRepository
                .findByTokenAndAtivoTrue(token)
                .orElseThrow(() ->
                        new AcessoNegadoException(
                                "Link de compartilhamento inválido ou revogado."
                        )
                );
    }

    public ResumoOrcamento buscarOrcamentoPorToken(String token) {

        LinkCompartilhamento link =
                buscarPorToken(token);

        Long viagemId =
                link.getViagem().getId();

        return orcamentoService.gerarResumo(viagemId);
    }

    public List<DiaRoteiroCompartilhadoResposta> buscarRoteiroPorToken(
            String token) {

        LinkCompartilhamento link =
                buscarPorToken(token);

        Long viagemId =
                link.getViagem().getId();

        List<DiaRoteiro> dias =
                diaRoteiroRepository
                        .findByViagemIdOrderByDataAsc(viagemId);

        List<DiaRoteiroCompartilhadoResposta> resposta =
                new ArrayList<>();

        for (DiaRoteiro dia : dias) {

            List<ItemRoteiroCompartilhadoResposta> itens =
                    itemRoteiroRepository
                            .findByDiaRoteiroIdOrderByOrdemAsc(
                                    dia.getId()
                            )
                            .stream()
                            .map(ItemRoteiroCompartilhadoResposta::new)
                            .toList();

            DiaRoteiroCompartilhadoResposta diaResposta =
                    new DiaRoteiroCompartilhadoResposta(
                            dia,
                            itens
                    );

            resposta.add(diaResposta);
        }

        return resposta;
    }

    @Transactional
    public LinkCompartilhamento revogarLink(
            Long linkId,
            Long usuarioSolicitanteId) {

        LinkCompartilhamento link =
                linkRepository.findById(linkId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Link de compartilhamento não encontrado."
                                )
                        );

        validarProprietario(
                link.getViagem(),
                usuarioSolicitanteId
        );

        if (!Boolean.TRUE.equals(link.getAtivo())) {
            throw new RuntimeException(
                    "Este link de compartilhamento já foi revogado."
            );
        }

        link.setAtivo(false);
        link.setDataRevogacao(LocalDateTime.now());

        return linkRepository.save(link);
    }

    public List<LinkCompartilhamento> listarPorViagem(
            Long viagemId,
            Long usuarioSolicitanteId) {

        Viagem viagem =
                buscarViagem(viagemId);

        validarProprietario(
                viagem,
                usuarioSolicitanteId
        );

        return linkRepository
                .findByViagemIdAndAtivoTrue(viagemId);
    }

    public EstimativaRotaCompartilhadaResposta buscarEstimativaRotaPorToken(
            String token) {

        LinkCompartilhamento link =
                buscarPorToken(token);

        Long viagemId =
                link.getViagem().getId();

        EstimativaRota estimativa =
                estimativaRotaRepository
                        .findFirstByViagemIdOrderByDataConsultaDesc(viagemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Nenhuma estimativa de rota encontrada para esta viagem."
                                )
                        );

        return new EstimativaRotaCompartilhadaResposta(
                estimativa
        );
    }

    private Viagem buscarViagem(Long viagemId) {

        return viagemRepository.findById(viagemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Viagem não encontrada."
                        )
                );
    }

    private void validarProprietario(
            Viagem viagem,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirSolicitanteAutenticado(usuarioSolicitanteId);

        if (!viagem.getUsuario()
                .getId()
                .equals(usuarioSolicitanteId)) {

            throw new AcessoNegadoException(
                    "Somente o proprietário da viagem pode gerenciar o link de compartilhamento."
            );
        }
    }

    private String gerarToken() {

        return UUID.randomUUID().toString();
    }
}
