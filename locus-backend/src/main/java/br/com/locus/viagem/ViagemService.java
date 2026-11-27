package br.com.locus.viagem;

import br.com.locus.admin.ControleAcessoService;
import br.com.locus.compartilhamento.PermissaoCompartilhamentoService;
import br.com.locus.perfil.PerfilViajante;
import br.com.locus.perfil.PerfilViajanteRepository;
import br.com.locus.perfil.PreferenciaTransporte;
import br.com.locus.perfil.PreferenciaTransporteRepository;
import br.com.locus.perfil.PrioridadePreferencia;
import br.com.locus.perfil.TipoOrcamento;
import br.com.locus.notificacao.NotificacaoService;
import br.com.locus.notificacao.TipoNotificacao;
import br.com.locus.notificacao.dto.NotificacaoRequest;
import br.com.locus.usuario.Usuario;
import br.com.locus.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Service
public class ViagemService {

    private final ViagemRepository viagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegraTransporteService regraTransporteService;
    private final PerfilViajanteRepository perfilViajanteRepository;
    private final PreferenciaTransporteRepository preferenciaTransporteRepository;
    private final ControleAcessoService controleAcessoService;
    private final PermissaoCompartilhamentoService permissaoCompartilhamentoService;
    private final NotificacaoService notificacaoService;

    public ViagemService(
            ViagemRepository viagemRepository,
            UsuarioRepository usuarioRepository,
            RegraTransporteService regraTransporteService,
            PerfilViajanteRepository perfilViajanteRepository,
            PreferenciaTransporteRepository preferenciaTransporteRepository,
            ControleAcessoService controleAcessoService,
            PermissaoCompartilhamentoService permissaoCompartilhamentoService,
            NotificacaoService notificacaoService) {

        this.viagemRepository = viagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.regraTransporteService = regraTransporteService;
        this.perfilViajanteRepository = perfilViajanteRepository;
        this.preferenciaTransporteRepository = preferenciaTransporteRepository;
        this.controleAcessoService = controleAcessoService;
        this.permissaoCompartilhamentoService =
                permissaoCompartilhamentoService;
        this.notificacaoService = notificacaoService;
    }

    public Viagem salvar(
            Long usuarioId,
            Long usuarioSolicitanteId,
            Viagem viagem) {

        controleAcessoService.exigirMesmoUsuario(
                usuarioId,
                usuarioSolicitanteId
        );

        Usuario usuario =
                usuarioRepository.findById(usuarioId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Usuário não encontrado."
                                ));

        if (viagem.getOrigem() == null
                || viagem.getOrigem().isBlank()) {

            throw new RuntimeException(
                    "A origem da viagem é obrigatória."
            );
        }

        if (viagem.getDestino() == null
                || viagem.getDestino().isBlank()) {

            throw new RuntimeException(
                    "O destino da viagem é obrigatório."
            );
        }

        if (viagem.getTitulo() == null || viagem.getTitulo().isBlank()) {
            viagem.setTitulo("Roteiro para " + viagem.getDestino().trim());
        }

        if (viagem.getTipoOrcamento() == null) {
            viagem.setTipoOrcamento(TipoOrcamento.ABERTO);
        }

        if (viagem.getTipoOrcamento() == TipoOrcamento.FIXO) {
            if (viagem.getValorOrcamentoTotal() == null
                    || viagem.getValorOrcamentoTotal().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException(
                        "Para orçamento fixo, informe o valor total da viagem."
                );
            }
        } else {
            viagem.setValorOrcamentoTotal(null);
        }

        if (viagem.getOrigem()
                .trim()
                .equalsIgnoreCase(
                        viagem.getDestino().trim()
                )) {

            throw new RuntimeException(
                    "A origem e o destino da viagem devem ser diferentes."
            );
        }

        if (viagem.getDataInicio() == null) {
            throw new RuntimeException(
                    "A data de início da viagem é obrigatória."
            );
        }

        if (viagem.getDataFim() == null) {
            throw new RuntimeException(
                    "A data de fim da viagem é obrigatória."
            );
        }

        if (viagem.getDataFim()
                .isBefore(viagem.getDataInicio())) {

            throw new RuntimeException(
                    "A data de fim não pode ser anterior à data de início."
            );
        }

        boolean existeConflito = viagemRepository
                .findByUsuarioIdAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                        usuarioId,
                        viagem.getDataFim(),
                        viagem.getDataInicio())
                .stream()
                .anyMatch(item -> !viagem.isMochilao());

        if (existeConflito) {
            throw new RuntimeException(
                    "Já existe uma viagem no mesmo período. Marque como mochilão para organizar vários destinos na mesma janela."
            );
        }

        if (viagem.getModoTransporte() == null) {
            throw new RuntimeException(
                    "O modo de transporte é obrigatório."
            );
        }

        PerfilViajante perfilViajante =
                perfilViajanteRepository
                        .findByUsuarioId(usuarioId)
                        .orElse(null);

        if (perfilViajante != null) {

            PreferenciaTransporte preferenciaTransporte =
                    preferenciaTransporteRepository
                            .findByPerfilViajanteId(
                                    perfilViajante.getId()
                            )
                            .orElse(null);

            if (preferenciaTransporte != null
                    && preferenciaTransporte.getPrioridade()
                    == PrioridadePreferencia.OBRIGATORIA
                    && preferenciaTransporte.getModoTransporte()
                    != viagem.getModoTransporte()) {

                throw new RuntimeException(
                        "O modo de transporte da viagem deve respeitar a preferência obrigatória do viajante."
                );
            }
        }

        if (viagem.getViabilidadeTransporteTerrestre() == null) {

            viagem.setViabilidadeTransporteTerrestre(
                    ViabilidadeTransporteTerrestre.NAO_VERIFICADA
            );
        }

        regraTransporteService.validar(
                viagem.getModoTransporte(),
                viagem.getViabilidadeTransporteTerrestre()
        );

        viagem.setUsuario(usuario);

        Viagem viagemSalva = viagemRepository.save(viagem);
        NotificacaoRequest notificacao = new NotificacaoRequest();
        notificacao.setTipo(TipoNotificacao.VIAGEM);
        notificacao.setTitulo("Viagem criada");
        notificacao.setMensagem("Seu roteiro para " + viagemSalva.getDestino() + " já está pronto para receber escolhas.");
        notificacaoService.criar(usuarioId, notificacao);
        return viagemSalva;
    }

    public List<Viagem> listarPorUsuario(
            Long usuarioId,
            Long usuarioSolicitanteId) {

        controleAcessoService.exigirMesmoUsuario(
                usuarioId,
                usuarioSolicitanteId
        );

        return viagemRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Viagem> buscarPorId(
            Long id,
            Long usuarioSolicitanteId) {

        permissaoCompartilhamentoService.validarVisualizacao(
                id,
                usuarioSolicitanteId
        );

        Viagem viagem =
                viagemRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Viagem não encontrada."
                                ));

        return Optional.of(viagem);
    }

    public Viagem atualizar(
            Long viagemId,
            Long usuarioSolicitanteId,
            Viagem dados) {

        Viagem atual = controleAcessoService.exigirProprietarioViagem(
                viagemId,
                usuarioSolicitanteId
        );

        if (dados.getOrigem() != null && !dados.getOrigem().isBlank()) {
            atual.setOrigem(dados.getOrigem().trim());
        }
        if (dados.getDestino() != null && !dados.getDestino().isBlank()) {
            atual.setDestino(dados.getDestino().trim());
        }
        if (dados.getTitulo() != null && !dados.getTitulo().isBlank()) {
            atual.setTitulo(dados.getTitulo().trim());
        }
        if (dados.getDataInicio() != null) atual.setDataInicio(dados.getDataInicio());
        if (dados.getDataFim() != null) atual.setDataFim(dados.getDataFim());
        if (dados.getModoTransporte() != null) atual.setModoTransporte(dados.getModoTransporte());
        if (dados.getTipoOrcamento() != null) atual.setTipoOrcamento(dados.getTipoOrcamento());
        if (dados.isMochilaoInformado()) {
            atual.setMochilao(dados.isMochilao());
        }
        if (atual.getTipoOrcamento() == TipoOrcamento.FIXO) {
            if (dados.getValorOrcamentoTotal() != null) {
                atual.setValorOrcamentoTotal(dados.getValorOrcamentoTotal());
            }
            if (atual.getValorOrcamentoTotal() == null
                    || atual.getValorOrcamentoTotal().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Para orçamento fixo, informe o valor total da viagem.");
            }
        } else {
            atual.setValorOrcamentoTotal(null);
        }
        if (atual.getDataInicio() != null && atual.getDataFim() != null
                && atual.getDataFim().isBefore(atual.getDataInicio())) {
            throw new RuntimeException("A data de fim não pode ser anterior à data de início.");
        }
        boolean existeConflito = atual.getDataInicio() != null && atual.getDataFim() != null
                && viagemRepository
                .findByUsuarioIdAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                        atual.getUsuario().getId(),
                        atual.getDataFim(),
                        atual.getDataInicio())
                .stream()
                .anyMatch(item -> !item.getId().equals(atual.getId()) && !atual.isMochilao());
        if (existeConflito) {
            throw new RuntimeException(
                    "Já existe uma viagem no mesmo período. Marque como mochilão para organizar vários destinos na mesma janela."
            );
        }
        return viagemRepository.save(atual);
    }
}
