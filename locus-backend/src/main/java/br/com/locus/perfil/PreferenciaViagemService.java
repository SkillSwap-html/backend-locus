package br.com.locus.perfil;

import br.com.locus.admin.ControleAcessoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PreferenciaViagemService {

    private final PreferenciaViagemRepository preferenciaViagemRepository;
    private final PerfilViajanteRepository perfilViajanteRepository;
    private final ControleAcessoService controleAcessoService;

    public PreferenciaViagemService(
            PreferenciaViagemRepository preferenciaViagemRepository,
            PerfilViajanteRepository perfilViajanteRepository,
            ControleAcessoService controleAcessoService) {

        this.preferenciaViagemRepository = preferenciaViagemRepository;
        this.perfilViajanteRepository = perfilViajanteRepository;
        this.controleAcessoService = controleAcessoService;
    }

    public PreferenciaViagem salvar(
            Long perfilId,
            Long usuarioSolicitanteId,
            PreferenciaViagem preferenciaViagem) {

        PerfilViajante perfilViajante =
                perfilViajanteRepository.findById(perfilId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Perfil de viajante não encontrado."
                                ));

        controleAcessoService.exigirMesmoUsuario(
                perfilViajante.getUsuario().getId(),
                usuarioSolicitanteId
        );

        if (preferenciaViagem.getTipoOrcamento() == null) {
            throw new RuntimeException(
                    "O tipo de orçamento é obrigatório."
            );
        }

        if (preferenciaViagem.getTipoOrcamento()
                == TipoOrcamento.FIXO) {

            if (preferenciaViagem.getValorOrcamento() == null
                    || preferenciaViagem.getValorOrcamento()
                    .compareTo(BigDecimal.ZERO) <= 0) {

                throw new RuntimeException(
                        "Para orçamento fixo, informe um valor maior que zero."
                );
            }
        }

        if (preferenciaViagem.getTipoOrcamento()
                == TipoOrcamento.ABERTO) {

            preferenciaViagem.setValorOrcamento(null);
        }

        PreferenciaViagem preferenciaParaSalvar = preferenciaViagemRepository
                .findByPerfilViajanteId(perfilId)
                .orElseGet(PreferenciaViagem::new);
        preferenciaParaSalvar.setPerfilViajante(perfilViajante);
        preferenciaParaSalvar.setOrigemPreferida(preferenciaViagem.getOrigemPreferida());
        preferenciaParaSalvar.setDestinoPreferido(preferenciaViagem.getDestinoPreferido());
        preferenciaParaSalvar.setTipoOrcamento(preferenciaViagem.getTipoOrcamento());
        preferenciaParaSalvar.setValorOrcamento(preferenciaViagem.getValorOrcamento());

        return preferenciaViagemRepository.save(preferenciaParaSalvar);
    }

    public Optional<PreferenciaViagem> buscarPorPerfil(
            Long perfilId,
            Long usuarioSolicitanteId) {

        PerfilViajante perfilViajante =
                perfilViajanteRepository.findById(perfilId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Perfil de viajante não encontrado."
                                ));

        controleAcessoService.exigirMesmoUsuario(
                perfilViajante.getUsuario().getId(),
                usuarioSolicitanteId
        );

        return preferenciaViagemRepository
                .findByPerfilViajanteId(perfilId);
    }

    public PreferenciaViagem buscarOuCriarPadrao(
            Long perfilId,
            Long usuarioSolicitanteId) {

        Optional<PreferenciaViagem> existente = buscarPorPerfil(
                perfilId,
                usuarioSolicitanteId
        );
        if (existente.isPresent()) {
            return existente.get();
        }

        PerfilViajante perfilViajante = perfilViajanteRepository
                .findById(perfilId)
                .orElseThrow(() -> new RuntimeException(
                        "Perfil de viajante não encontrado."
                ));
        PreferenciaViagem padrao = new PreferenciaViagem();
        padrao.setPerfilViajante(perfilViajante);
        padrao.setTipoOrcamento(TipoOrcamento.ABERTO);
        return preferenciaViagemRepository.save(padrao);
    }
}
