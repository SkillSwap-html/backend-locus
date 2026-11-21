package br.com.locus.perfil;

import br.com.locus.admin.ControleAcessoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PerfilVeiculoService {

    private final PerfilVeiculoRepository perfilVeiculoRepository;
    private final PerfilViajanteRepository perfilViajanteRepository;
    private final ControleAcessoService controleAcessoService;

    public PerfilVeiculoService(
            PerfilVeiculoRepository perfilVeiculoRepository,
            PerfilViajanteRepository perfilViajanteRepository,
            ControleAcessoService controleAcessoService) {

        this.perfilVeiculoRepository = perfilVeiculoRepository;
        this.perfilViajanteRepository = perfilViajanteRepository;
        this.controleAcessoService = controleAcessoService;
    }

    public PerfilVeiculo salvar(
            Long perfilId,
            Long usuarioSolicitanteId,
            PerfilVeiculo perfilVeiculo) {

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

        if (perfilVeiculo.getTipoVeiculo() == null
                || perfilVeiculo.getTipoVeiculo().isBlank()) {

            throw new RuntimeException(
                    "O tipo do veículo é obrigatório."
            );
        }

        if (perfilVeiculo.getTipoCombustivel() == null
                || perfilVeiculo.getTipoCombustivel().isBlank()) {

            throw new RuntimeException(
                    "O tipo de combustível é obrigatório."
            );
        }

        if (perfilVeiculo.getConsumoMedio() == null
                || perfilVeiculo.getConsumoMedio() <= 0) {

            throw new RuntimeException(
                    "O consumo médio deve ser maior que zero."
            );
        }

        PerfilVeiculo perfilParaSalvar = perfilVeiculoRepository
                .findByPerfilViajanteId(perfilId)
                .orElseGet(PerfilVeiculo::new);
        perfilParaSalvar.setPerfilViajante(perfilViajante);
        perfilParaSalvar.setTipoVeiculo(perfilVeiculo.getTipoVeiculo());
        perfilParaSalvar.setTipoCombustivel(perfilVeiculo.getTipoCombustivel());
        perfilParaSalvar.setConsumoMedio(perfilVeiculo.getConsumoMedio());

        return perfilVeiculoRepository.save(perfilParaSalvar);
    }

    public Optional<PerfilVeiculo> buscarPorPerfil(
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

        return perfilVeiculoRepository
                .findByPerfilViajanteId(perfilId);
    }
}
