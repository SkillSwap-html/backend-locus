package br.com.locus.lugar;

import br.com.locus.admin.ControleAcessoService;
import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.auditoria.ResultadoAuditoria;
import br.com.locus.compartilhamento.PermissaoCompartilhamentoService;
import br.com.locus.lugar.dto.AtualizarLugarRequest;
import br.com.locus.viagem.Destino;
import br.com.locus.viagem.DestinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LugarService {

    private final LugarRepository lugarRepository;
    private final DestinoRepository destinoRepository;
    private final ControleAcessoService controleAcessoService;
    private final EventoAuditoriaService eventoAuditoriaService;
    private final PermissaoCompartilhamentoService permissaoCompartilhamentoService;

    public LugarService(
            LugarRepository lugarRepository,
            DestinoRepository destinoRepository,
            ControleAcessoService controleAcessoService,
            EventoAuditoriaService eventoAuditoriaService,
            PermissaoCompartilhamentoService permissaoCompartilhamentoService) {

        this.lugarRepository = lugarRepository;
        this.destinoRepository = destinoRepository;
        this.controleAcessoService = controleAcessoService;
        this.eventoAuditoriaService = eventoAuditoriaService;
        this.permissaoCompartilhamentoService = permissaoCompartilhamentoService;
    }

    public Lugar salvar(Long destinoId, Lugar lugar) {

        Destino destino = destinoRepository.findById(destinoId)
                .orElseThrow(() ->
                        new RuntimeException("Destino não encontrado.")
                );

        if (lugar.getNome() == null || lugar.getNome().isBlank()) {
            throw new RuntimeException(
                    "O nome do lugar é obrigatório."
            );
        }

        lugar.setDestino(destino);

        return lugarRepository.save(lugar);
    }

    public Lugar salvar(
            Long destinoId,
            Long usuarioSolicitanteId,
            Lugar lugar) {

        controleAcessoService.exigirCuradorOuAdministrador(
                usuarioSolicitanteId
        );

        return salvar(destinoId, lugar);
    }

    public List<Lugar> listarPorDestino(Long destinoId) {

        return lugarRepository.findByDestinoId(destinoId);
    }

    public List<Lugar> listarPorDestino(
            Long destinoId,
            Long usuarioSolicitanteId) {

        Destino destino = destinoRepository.findById(destinoId)
                .orElseThrow(() ->
                        new RuntimeException("Destino não encontrado.")
                );

        permissaoCompartilhamentoService.validarVisualizacao(
                destino.getViagem().getId(),
                usuarioSolicitanteId
        );

        return listarPorDestino(destinoId);
    }

    public Optional<Lugar> buscarPorId(Long id) {

        return lugarRepository.findById(id);
    }

    public Optional<Lugar> buscarPorId(
            Long id,
            Long usuarioSolicitanteId) {

        Optional<Lugar> lugar = lugarRepository.findById(id);

        lugar.ifPresent(l ->
                permissaoCompartilhamentoService.validarVisualizacao(
                        l.getDestino().getViagem().getId(),
                        usuarioSolicitanteId
                )
        );

        return lugar;
    }

    @Transactional
    public Lugar atualizarLugar(
            Long lugarId,
            AtualizarLugarRequest request,
            Long usuarioSolicitanteId) {

        try {

            controleAcessoService.exigirCuradorOuAdministrador(
                    usuarioSolicitanteId
            );

            Lugar lugar = lugarRepository.findById(lugarId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Lugar não encontrado."
                            )
                    );

            if (request.getNome() != null) {

                if (request.getNome().isBlank()) {
                    throw new RuntimeException(
                            "O nome do lugar não pode ficar vazio."
                    );
                }

                lugar.setNome(request.getNome());
            }

            if (request.getCategoria() != null) {
                lugar.setCategoria(request.getCategoria());
            }

            if (request.getEndereco() != null) {
                lugar.setEndereco(request.getEndereco());
            }

            if (request.getLatitude() != null) {
                lugar.setLatitude(request.getLatitude());
            }

            if (request.getLongitude() != null) {
                lugar.setLongitude(request.getLongitude());
            }

            Lugar lugarSalvo =
                    lugarRepository.save(lugar);

            eventoAuditoriaService.registrar(
                    usuarioSolicitanteId,
                    "ATUALIZAR_LUGAR",
                    "LUGAR",
                    lugarSalvo.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Dados do lugar "
                            + lugarSalvo.getNome()
                            + " foram atualizados."
            );

            return lugarSalvo;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioSolicitanteId,
                        "ATUALIZAR_LUGAR",
                        "LUGAR",
                        lugarId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
            }

            throw e;
        }
    }
}