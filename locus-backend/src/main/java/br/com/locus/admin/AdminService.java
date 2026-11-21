package br.com.locus.admin;

import br.com.locus.admin.dto.UsuarioAdminResposta;
import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.auditoria.ResultadoAuditoria;
import br.com.locus.usuario.TipoUsuario;
import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import br.com.locus.admin.dto.DashboardAdminResposta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final ViagemRepository viagemRepository;
    private final EventoAuditoriaService eventoAuditoriaService;

    public AdminService(
            UsuarioRepository usuarioRepository,
            ViagemRepository viagemRepository,
            EventoAuditoriaService eventoAuditoriaService) {

        this.usuarioRepository = usuarioRepository;
        this.viagemRepository = viagemRepository;
        this.eventoAuditoriaService = eventoAuditoriaService;
    }

    @Transactional
    public Usuario alterarTipoUsuario(
            Long usuarioId,
            TipoUsuario novoTipo,
            Long usuarioSolicitanteId) {

        try {

            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() ->
                            new RuntimeException("Usuário não encontrado.")
                    );

            if (novoTipo == null) {
                throw new RuntimeException(
                        "O novo tipo de usuário é obrigatório."
                );
            }

            TipoUsuario tipoAnterior = usuario.getTipoUsuario();

            if (tipoAnterior == TipoUsuario.ADMINISTRADOR
                    && novoTipo != TipoUsuario.ADMINISTRADOR) {

                long quantidadeAdministradores =
                        usuarioRepository.countByTipoUsuario(
                                TipoUsuario.ADMINISTRADOR
                        );

                if (quantidadeAdministradores <= 1) {
                    throw new RuntimeException(
                            "Não é possível alterar o último administrador do sistema."
                    );
                }
            }

            usuario.setTipoUsuario(novoTipo);

            Usuario usuarioSalvo =
                    usuarioRepository.save(usuario);

            eventoAuditoriaService.registrar(
                    usuarioSolicitanteId,
                    "ALTERAR_TIPO_USUARIO",
                    "USUARIO",
                    usuarioSalvo.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Tipo do usuário alterado de "
                            + tipoAnterior
                            + " para "
                            + novoTipo
            );

            return usuarioSalvo;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioSolicitanteId,
                        "ALTERAR_TIPO_USUARIO",
                        "USUARIO",
                        usuarioId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
            }

            throw e;
        }
    }

    public List<UsuarioAdminResposta> listarUsuarios() {

        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioAdminResposta::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public DashboardAdminResposta dashboard() {
        List<Viagem> viagens = viagemRepository.findAll();
        Map<String, Long> contagemPorDestino = new LinkedHashMap<>();
        Map<String, String> rotulosPorDestino = new LinkedHashMap<>();

        viagens.stream()
                .map(Viagem::getDestino)
                .filter(destino -> destino != null && !destino.isBlank())
                .map(String::trim)
                .forEach(destino -> {
                    String chave = destino.toLowerCase(Locale.ROOT);
                    rotulosPorDestino.putIfAbsent(chave, destino);
                    contagemPorDestino.merge(chave, 1L, Long::sum);
                });

        List<DashboardAdminResposta.DestinoDashboard> destinos = contagemPorDestino.entrySet().stream()
                .sorted((esquerda, direita) -> {
                    int porQuantidade = Long.compare(direita.getValue(), esquerda.getValue());
                    return porQuantidade != 0
                            ? porQuantidade
                            : esquerda.getKey().compareTo(direita.getKey());
                })
                .map(item -> new DashboardAdminResposta.DestinoDashboard(rotulosPorDestino.get(item.getKey()), item.getValue()))
                .toList();

        return new DashboardAdminResposta(
                usuarioRepository.count(),
                viagens.size(),
                destinos.size(),
                destinos);
    }
}
