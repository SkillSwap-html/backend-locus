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

        if (preferenciaViagemRepository
                .existsByPerfilViajanteId(perfilId)) {

            throw new RuntimeException(
                    "Este perfil já possui preferências de viagem."
            );
        }

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

        preferenciaViagem.setPerfilViajante(perfilViajante);

        return preferenciaViagemRepository.save(preferenciaViagem);
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
}