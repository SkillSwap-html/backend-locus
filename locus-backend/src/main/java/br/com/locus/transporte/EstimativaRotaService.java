package br.com.locus.transporte;

import br.com.locus.perfil.ModoTransporte;
import br.com.locus.perfil.PerfilVeiculo;
import br.com.locus.perfil.PerfilVeiculoRepository;
import br.com.locus.perfil.PerfilViajante;
import br.com.locus.perfil.PerfilViajanteRepository;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import org.springframework.stereotype.Service;
import br.com.locus.admin.ControleAcessoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class EstimativaRotaService {

    private final EstimativaRotaRepository estimativaRotaRepository;
    private final ViagemRepository viagemRepository;
    private final PerfilViajanteRepository perfilViajanteRepository;
    private final PerfilVeiculoRepository perfilVeiculoRepository;
    private final ControleAcessoService controleAcessoService;

    public EstimativaRotaService(
            EstimativaRotaRepository estimativaRotaRepository,
            ViagemRepository viagemRepository,
            PerfilViajanteRepository perfilViajanteRepository,
            PerfilVeiculoRepository perfilVeiculoRepository,
            ControleAcessoService controleAcessoService) {

        this.estimativaRotaRepository = estimativaRotaRepository;
        this.viagemRepository = viagemRepository;
        this.perfilViajanteRepository = perfilViajanteRepository;
        this.perfilVeiculoRepository = perfilVeiculoRepository;
        this.controleAcessoService = controleAcessoService;
    }

    public EstimativaRota salvar(
            Long viagemId,
            Long usuarioSolicitanteId,
            EstimativaRota estimativaRota) {

        Viagem viagem =
                controleAcessoService.exigirProprietarioViagem(
                        viagemId,
                        usuarioSolicitanteId
                );

        validarEstimativa(estimativaRota);

        if (!viagem.getModoTransporte()
                .equals(estimativaRota.getModoTransporte())) {

            throw new RuntimeException(
                    "O modo de transporte da estimativa deve ser o mesmo da viagem."
            );
        }

        if (ehTransporteTerrestreProprio(
                estimativaRota.getModoTransporte())) {

            calcularCombustivel(
                    viagem,
                    estimativaRota
            );

        } else {

            estimativaRota.setPrecoCombustivelLitro(null);
            estimativaRota.setConsumoMedioUtilizado(null);
            estimativaRota.setCustoCombustivel(null);
        }

        BigDecimal custoCombustivel =
                valorOuZero(
                        estimativaRota.getCustoCombustivel()
                );

        BigDecimal custoPedagios =
                valorOuZero(
                        estimativaRota.getCustoPedagios()
                );

        BigDecimal custoEstacionamento =
                valorOuZero(
                        estimativaRota.getCustoEstacionamento()
                );

        BigDecimal custoTotal =
                custoCombustivel
                        .add(custoPedagios)
                        .add(custoEstacionamento);

        estimativaRota.setCustoTotalEstimado(
                custoTotal
        );

        if (estimativaRota.getQuantidadePassageiros() != null
                && estimativaRota.getQuantidadePassageiros() > 0) {

            BigDecimal quantidade =
                    BigDecimal.valueOf(
                            estimativaRota.getQuantidadePassageiros()
                    );

            BigDecimal custoPorPassageiro =
                    custoTotal.divide(
                            quantidade,
                            2,
                            RoundingMode.HALF_UP
                    );

            estimativaRota.setCustoPorPassageiro(
                    custoPorPassageiro
            );

        } else {

            estimativaRota.setCustoPorPassageiro(null);
        }

        estimativaRota.setViagem(viagem);
        estimativaRota.setDataConsulta(
                LocalDateTime.now()
        );

        return estimativaRotaRepository.save(
                estimativaRota
        );
    }

    private void calcularCombustivel(
            Viagem viagem,
            EstimativaRota estimativaRota) {

        PerfilViajante perfilViajante =
                perfilViajanteRepository
                        .findByUsuarioId(
                                viagem.getUsuario().getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Perfil de viajante não encontrado."
                                ));

        PerfilVeiculo perfilVeiculo =
                perfilVeiculoRepository
                        .findByPerfilViajanteId(
                                perfilViajante.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cadastre um veículo antes de calcular uma rota terrestre."
                                ));

        if (perfilVeiculo.getConsumoMedio() == null
                || perfilVeiculo.getConsumoMedio() <= 0) {

            throw new RuntimeException(
                    "O veículo deve possuir um consumo médio válido."
            );
        }

        if (estimativaRota.getPrecoCombustivelLitro() == null
                || estimativaRota.getPrecoCombustivelLitro()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "O preço do combustível por litro deve ser informado."
            );
        }

        BigDecimal consumoMedio =
                BigDecimal.valueOf(
                        perfilVeiculo.getConsumoMedio()
                );

        BigDecimal litrosNecessarios =
                estimativaRota.getDistanciaKm()
                        .divide(
                                consumoMedio,
                                4,
                                RoundingMode.HALF_UP
                        );

        BigDecimal custoCombustivel =
                litrosNecessarios
                        .multiply(
                                estimativaRota.getPrecoCombustivelLitro()
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        estimativaRota.setConsumoMedioUtilizado(
                consumoMedio
        );

        estimativaRota.setCustoCombustivel(
                custoCombustivel
        );
    }

    public List<EstimativaRota> listarPorViagem(
            Long viagemId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        return estimativaRotaRepository
                .findByViagemId(viagemId);
    }

    private void validarEstimativa(
            EstimativaRota estimativaRota) {

        if (estimativaRota.getModoTransporte() == null) {

            throw new RuntimeException(
                    "O modo de transporte é obrigatório."
            );
        }

        if (estimativaRota.getDistanciaKm() == null
                || estimativaRota.getDistanciaKm()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "A distância não pode ser negativa."
            );
        }

        if (estimativaRota.getDuracaoMinutos() == null
                || estimativaRota.getDuracaoMinutos() < 0) {

            throw new RuntimeException(
                    "A duração não pode ser negativa."
            );
        }

        if (estimativaRota.getFonteEstimativa() == null) {

            throw new RuntimeException(
                    "A fonte da estimativa é obrigatória."
            );
        }

        validarValorNaoNegativo(
                estimativaRota.getCustoPedagios(),
                "O custo de pedágios não pode ser negativo."
        );

        validarValorNaoNegativo(
                estimativaRota.getCustoEstacionamento(),
                "O custo de estacionamento não pode ser negativo."
        );

        if (estimativaRota.getQuantidadePassageiros() != null
                && estimativaRota.getQuantidadePassageiros() <= 0) {

            throw new RuntimeException(
                    "A quantidade de passageiros deve ser maior que zero."
            );
        }

        validarCustosConformeModo(
                estimativaRota
        );
    }

    private void validarCustosConformeModo(
            EstimativaRota estimativaRota) {

        if (!ehTransporteTerrestreProprio(
                estimativaRota.getModoTransporte())) {

            BigDecimal pedagios =
                    valorOuZero(
                            estimativaRota.getCustoPedagios()
                    );

            BigDecimal estacionamento =
                    valorOuZero(
                            estimativaRota.getCustoEstacionamento()
                    );

            if (pedagios.compareTo(BigDecimal.ZERO) > 0
                    || estacionamento.compareTo(BigDecimal.ZERO) > 0) {

                throw new RuntimeException(
                        "Custos de pedágios e estacionamento só podem ser informados para carro, motocicleta ou veículo particular."
                );
            }
        }
    }

    private boolean ehTransporteTerrestreProprio(
            ModoTransporte modoTransporte) {

        return modoTransporte == ModoTransporte.CARRO
                || modoTransporte == ModoTransporte.MOTOCICLETA
                || modoTransporte == ModoTransporte.VEICULO_PARTICULAR;
    }

    private void validarValorNaoNegativo(
            BigDecimal valor,
            String mensagem) {

        if (valor != null
                && valor.compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(mensagem);
        }
    }

    private BigDecimal valorOuZero(
            BigDecimal valor) {

        if (valor == null) {
            return BigDecimal.ZERO;
        }

        return valor;
    }
}