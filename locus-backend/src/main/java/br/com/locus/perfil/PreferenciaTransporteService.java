package br.com.locus.perfil;

import br.com.locus.admin.ControleAcessoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PreferenciaTransporteService {

    private final PreferenciaTransporteRepository preferenciaTransporteRepository;
    private final PerfilViajanteRepository perfilViajanteRepository;
    private final ControleAcessoService controleAcessoService;

    public PreferenciaTransporteService(
            PreferenciaTransporteRepository preferenciaTransporteRepository,
            PerfilViajanteRepository perfilViajanteRepository,
            ControleAcessoService controleAcessoService) {

        this.preferenciaTransporteRepository = preferenciaTransporteRepository;
        this.perfilViajanteRepository = perfilViajanteRepository;
        this.controleAcessoService = controleAcessoService;
    }

    public PreferenciaTransporte salvar(
            Long perfilId,
            Long usuarioSolicitanteId,
            PreferenciaTransporte preferenciaTransporte) {

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

        if (preferenciaTransporte.getModoTransporte() == null) {
            throw new RuntimeException(
                    "O modo de transporte é obrigatório."
            );
        }

        if (preferenciaTransporte.getPrioridade() == null) {
            preferenciaTransporte.setPrioridade(
                    PrioridadePreferencia.INDIFERENTE
            );
        }

        PreferenciaTransporte preferenciaParaSalvar = preferenciaTransporteRepository
                .findByPerfilViajanteId(perfilId)
                .orElseGet(PreferenciaTransporte::new);
        preferenciaParaSalvar.setPerfilViajante(perfilViajante);
        preferenciaParaSalvar.setModoTransporte(preferenciaTransporte.getModoTransporte());
        preferenciaParaSalvar.setPrioridade(preferenciaTransporte.getPrioridade());

        return preferenciaTransporteRepository.save(
                preferenciaParaSalvar
        );
    }

    public Optional<PreferenciaTransporte> buscarPorPerfil(
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

        return preferenciaTransporteRepository
                .findByPerfilViajanteId(perfilId);
    }

    public PreferenciaTransporte buscarOuCriarPadrao(
            Long perfilId,
            Long usuarioSolicitanteId) {

        Optional<PreferenciaTransporte> existente = buscarPorPerfil(
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
        PreferenciaTransporte padrao = new PreferenciaTransporte();
        padrao.setPerfilViajante(perfilViajante);
        padrao.setModoTransporte(ModoTransporte.AVIAO);
        padrao.setPrioridade(PrioridadePreferencia.INDIFERENTE);
        return preferenciaTransporteRepository.save(padrao);
    }
}
