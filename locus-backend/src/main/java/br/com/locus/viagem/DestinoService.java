package br.com.locus.viagem;

import br.com.locus.compartilhamento.PermissaoCompartilhamentoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinoService {

    private final DestinoRepository destinoRepository;
    private final ViagemRepository viagemRepository;
    private final PermissaoCompartilhamentoService permissaoCompartilhamentoService;

    public DestinoService(
            DestinoRepository destinoRepository,
            ViagemRepository viagemRepository,
            PermissaoCompartilhamentoService permissaoCompartilhamentoService) {

        this.destinoRepository = destinoRepository;
        this.viagemRepository = viagemRepository;
        this.permissaoCompartilhamentoService = permissaoCompartilhamentoService;
    }

    public Destino salvar(Long viagemId, Destino destino) {

        Viagem viagem = viagemRepository.findById(viagemId)
                .orElseThrow(() ->
                        new RuntimeException("Viagem não encontrada.")
                );

        if (destino.getCidade() == null || destino.getCidade().isBlank()) {
            throw new RuntimeException(
                    "A cidade do destino é obrigatória."
            );
        }

        if (destino.getPais() == null || destino.getPais().isBlank()) {
            throw new RuntimeException(
                    "O país do destino é obrigatório."
            );
        }

        destino.setViagem(viagem);

        return destinoRepository.save(destino);
    }

    public Destino salvar(
            Long viagemId,
            Long usuarioSolicitanteId,
            Destino destino) {

        permissaoCompartilhamentoService.validarEdicao(
                viagemId,
                usuarioSolicitanteId
        );

        return salvar(viagemId, destino);
    }

    public List<Destino> listarPorViagem(Long viagemId) {

        return destinoRepository.findByViagemId(viagemId);
    }

    public List<Destino> listarPorViagem(
            Long viagemId,
            Long usuarioSolicitanteId) {

        permissaoCompartilhamentoService.validarVisualizacao(
                viagemId,
                usuarioSolicitanteId
        );

        return listarPorViagem(viagemId);
    }

    public Optional<Destino> buscarPorId(Long id) {

        return destinoRepository.findById(id);
    }

    public Optional<Destino> buscarPorId(
            Long id,
            Long usuarioSolicitanteId) {

        Optional<Destino> destino =
                destinoRepository.findById(id);

        destino.ifPresent(d ->
                permissaoCompartilhamentoService.validarVisualizacao(
                        d.getViagem().getId(),
                        usuarioSolicitanteId
                )
        );

        return destino;
    }
}