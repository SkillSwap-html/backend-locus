package br.com.locus.roteiro;

import br.com.locus.compartilhamento.PermissaoCompartilhamentoService;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiaRoteiroService {

    private final DiaRoteiroRepository diaRoteiroRepository;
    private final ViagemRepository viagemRepository;
    private final PermissaoCompartilhamentoService permissaoCompartilhamentoService;

    public DiaRoteiroService(
            DiaRoteiroRepository diaRoteiroRepository,
            ViagemRepository viagemRepository,
            PermissaoCompartilhamentoService permissaoCompartilhamentoService) {

        this.diaRoteiroRepository = diaRoteiroRepository;
        this.viagemRepository = viagemRepository;
        this.permissaoCompartilhamentoService =
                permissaoCompartilhamentoService;
    }

    public DiaRoteiro salvar(
            Long viagemId,
            DiaRoteiro diaRoteiro) {

        Viagem viagem =
                viagemRepository.findById(viagemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Viagem não encontrada."
                                ));

        validarDiaRoteiro(
                viagem,
                diaRoteiro
        );

        // A criação dos dias é idempotente: o frontend pode garantir todos
        // os dias em paralelo sem duplicar a mesma data ao recarregar o
        // roteiro ou ao trocar de dia no assistente.
        Optional<DiaRoteiro> existente = diaRoteiroRepository
                .findByViagemIdAndData(viagemId, diaRoteiro.getData());
        if (existente.isPresent()) {
            return existente.get();
        }

        diaRoteiro.setViagem(viagem);

        return diaRoteiroRepository.save(
                diaRoteiro
        );
    }

    public DiaRoteiro salvar(
            Long viagemId,
            Long usuarioId,
            DiaRoteiro diaRoteiro) {

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioId
        );

        return salvar(
                viagemId,
                diaRoteiro
        );
    }

    public List<DiaRoteiro> listarPorViagem(
            Long viagemId) {

        return diaRoteiroRepository.findByViagemId(
                viagemId
        );
    }

    public List<DiaRoteiro> listarPorViagem(
            Long viagemId,
            Long usuarioId) {

        permissaoCompartilhamentoService.validarVisualizacao(
                viagemId,
                usuarioId
        );

        return listarPorViagem(
                viagemId
        );
    }

    public Optional<DiaRoteiro> buscarPorId(
            Long id) {

        return diaRoteiroRepository.findById(id);
    }

    public Optional<DiaRoteiro> buscarPorId(
            Long id,
            Long usuarioId) {

        DiaRoteiro diaRoteiro =
                diaRoteiroRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dia do roteiro não encontrado."
                                ));

        Long viagemId =
                diaRoteiro
                        .getViagem()
                        .getId();

        permissaoCompartilhamentoService.validarVisualizacao(
                viagemId,
                usuarioId
        );

        return Optional.of(
                diaRoteiro
        );
    }

    private void validarDiaRoteiro(
            Viagem viagem,
            DiaRoteiro diaRoteiro) {

        if (diaRoteiro.getData() == null) {

            throw new RuntimeException(
                    "A data do dia do roteiro é obrigatória."
            );
        }

        if (diaRoteiro.getData().isBefore(
                viagem.getDataInicio()
        )
                || diaRoteiro.getData().isAfter(
                viagem.getDataFim()
        )) {

            throw new RuntimeException(
                    "A data do roteiro deve estar dentro do período da viagem."
            );
        }
    }
}
