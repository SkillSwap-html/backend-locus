package br.com.locus.perfil;

import br.com.locus.admin.ControleAcessoService;
import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerfilViajanteService {

    private final PerfilViajanteRepository perfilViajanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ControleAcessoService controleAcessoService;

    public PerfilViajanteService(
            PerfilViajanteRepository perfilViajanteRepository,
            UsuarioRepository usuarioRepository,
            ControleAcessoService controleAcessoService) {

        this.perfilViajanteRepository = perfilViajanteRepository;
        this.usuarioRepository = usuarioRepository;
        this.controleAcessoService = controleAcessoService;
    }

    public PerfilViajante salvar(
            Long usuarioId,
            Long usuarioSolicitanteId,
            PerfilViajante perfilViajante) {

        controleAcessoService.exigirMesmoUsuario(
                usuarioId,
                usuarioSolicitanteId
        );

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado."));

        if (perfilViajante.getIdade() == null
                || perfilViajante.getIdade() <= 0) {

            throw new RuntimeException(
                    "A idade deve ser maior que zero."
            );
        }

        if (perfilViajante.getRitmoViagem() == null
                || perfilViajante.getRitmoViagem().isBlank()) {

            throw new RuntimeException(
                    "O ritmo da viagem é obrigatório."
            );
        }

        if (perfilViajante.getInteresses() == null
                || perfilViajante.getInteresses().isBlank()) {

            throw new RuntimeException(
                    "Os interesses do viajante são obrigatórios."
            );
        }

        if (perfilViajante.getMobilidade() == null
                || perfilViajante.getMobilidade().isBlank()) {

            throw new RuntimeException(
                    "As informações de mobilidade são obrigatórias."
            );
        }

        if (perfilViajante.getPreferenciaHospedagem() == null
                || perfilViajante.getPreferenciaHospedagem().isBlank()) {

            throw new RuntimeException(
                    "A preferência de hospedagem é obrigatória."
            );
        }

        PerfilViajante perfilParaSalvar = perfilViajanteRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(PerfilViajante::new);
        perfilParaSalvar.setUsuario(usuario);
        perfilParaSalvar.setIdade(perfilViajante.getIdade());
        perfilParaSalvar.setRitmoViagem(perfilViajante.getRitmoViagem());
        perfilParaSalvar.setInteresses(perfilViajante.getInteresses());
        perfilParaSalvar.setMobilidade(perfilViajante.getMobilidade());
        perfilParaSalvar.setPreferenciaHospedagem(perfilViajante.getPreferenciaHospedagem());

        return perfilViajanteRepository.save(perfilParaSalvar);
    }

    public List<PerfilViajante> listarTodos(
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirAdministrador(
                usuarioSolicitanteId
        );

        return perfilViajanteRepository.findAll();
    }

    public Optional<PerfilViajante> buscarPorUsuario(
            Long usuarioId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirMesmoUsuario(
                usuarioId,
                usuarioSolicitanteId
        );

        Optional<PerfilViajante> existente = perfilViajanteRepository.findByUsuarioId(usuarioId);
        if (existente.isPresent()) {
            return existente;
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        PerfilViajante perfil = new PerfilViajante();
        perfil.setUsuario(usuario);
        perfil.setIdade(18);
        perfil.setRitmoViagem("equilibrado");
        perfil.setInteresses("geral");
        perfil.setMobilidade("sem-restricao");
        perfil.setPreferenciaHospedagem("hotel");
        return Optional.of(perfilViajanteRepository.save(perfil));
    }
}
