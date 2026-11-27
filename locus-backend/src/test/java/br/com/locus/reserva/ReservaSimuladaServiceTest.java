package br.com.locus.reserva;

import br.com.locus.admin.ControleAcessoService;
import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.notificacao.NotificacaoService;
import br.com.locus.orcamento.OrcamentoService;
import br.com.locus.pagamento.PagamentoSimuladoRepository;
import br.com.locus.usuario.Usuario;
import br.com.locus.viagem.Viagem;
import br.com.locus.viagem.ViagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ReservaSimuladaServiceTest {

    private ReservaSimuladaRepository reservaSimuladaRepository;
    private ViagemRepository viagemRepository;
    private NotificacaoService notificacaoService;
    private EventoAuditoriaService eventoAuditoriaService;
    private ReservaSimuladaService reservaSimuladaService;

    @BeforeEach
    void prepararTeste() {
        reservaSimuladaRepository = mock(ReservaSimuladaRepository.class);
        viagemRepository = mock(ViagemRepository.class);

        OrcamentoService orcamentoService = mock(OrcamentoService.class);
        PagamentoSimuladoRepository pagamentoSimuladoRepository =
                mock(PagamentoSimuladoRepository.class);
        notificacaoService = mock(NotificacaoService.class);
        eventoAuditoriaService = mock(EventoAuditoriaService.class);
        ControleAcessoService controleAcessoService =
                mock(ControleAcessoService.class);

        reservaSimuladaService = new ReservaSimuladaService(
                reservaSimuladaRepository,
                viagemRepository,
                orcamentoService,
                pagamentoSimuladoRepository,
                notificacaoService,
                eventoAuditoriaService,
                controleAcessoService
        );
    }

    @Test
    void deveCriarReservaQuandoNaoExistirReservaAtiva() {
        Viagem viagem = mock(Viagem.class);
        ReservaSimulada reservaSalva = mock(ReservaSimulada.class);

        when(viagemRepository.findById(1L))
                .thenReturn(Optional.of(viagem));

        when(reservaSimuladaRepository
                .findFirstByViagemIdAndStatusNotOrderByIdDesc(
                        1L,
                        StatusReservaSimulada.CANCELADA_EM_SIMULACAO
                ))
                .thenReturn(Optional.empty());

        when(reservaSimuladaRepository.existsByProtocolo(any()))
                .thenReturn(false);

        when(reservaSimuladaRepository.save(any(ReservaSimulada.class)))
                .thenReturn(reservaSalva);

        ReservaSimulada resultado =
                reservaSimuladaService.criar(1L);

        assertSame(reservaSalva, resultado);

        verify(reservaSimuladaRepository, times(1))
                .save(any(ReservaSimulada.class));
    }

    @Test
    void deveRetornarReservaExistenteSemCriarOutra() {
        Viagem viagem = mock(Viagem.class);
        ReservaSimulada reservaExistente =
                mock(ReservaSimulada.class);

        when(viagemRepository.findById(1L))
                .thenReturn(Optional.of(viagem));

        when(reservaSimuladaRepository
                .findFirstByViagemIdAndStatusNotOrderByIdDesc(
                        1L,
                        StatusReservaSimulada.CANCELADA_EM_SIMULACAO
                ))
                .thenReturn(Optional.of(reservaExistente));

        ReservaSimulada resultado =
                reservaSimuladaService.criar(1L);

        assertSame(reservaExistente, resultado);

        verify(reservaSimuladaRepository, never())
                .save(any(ReservaSimulada.class));
    }

    @Test
    void deveConfirmarReservaAposPagamentoConcluido() {
        ReservaSimulada reserva = mock(ReservaSimulada.class);
        ReservaSimulada reservaSalva = mock(ReservaSimulada.class);
        Viagem viagem = mock(Viagem.class);
        Usuario usuario = mock(Usuario.class);

        when(reservaSimuladaRepository.findById(10L))
                .thenReturn(Optional.of(reserva));

        when(reserva.getViagem()).thenReturn(viagem);
        when(viagem.getUsuario()).thenReturn(usuario);
        when(usuario.getId()).thenReturn(2L);
        when(reserva.getId()).thenReturn(10L);
        when(reserva.getProtocolo()).thenReturn("LOCUS-TESTE");
        when(reserva.getStatus())
                .thenReturn(StatusReservaSimulada.PAGAMENTO_SIMULADO_CONCLUIDO);

        when(reservaSimuladaRepository.save(reserva))
                .thenReturn(reservaSalva);

        ReservaSimulada resultado =
                reservaSimuladaService.confirmar(10L);

        assertSame(reservaSalva, resultado);

        verify(reservaSimuladaRepository, times(1))
                .save(reserva);

        verify(notificacaoService, times(1))
                .criar(anyLong(), any());

        verify(eventoAuditoriaService, times(1))
                .registrar(
                        anyLong(),
                        any(),
                        any(),
                        anyLong(),
                        any(),
                        any()
                );
    }

    @Test
    void deveEvitarNovaConfirmacaoQuandoReservaJaEstiverConfirmada() {
        ReservaSimulada reserva = mock(ReservaSimulada.class);
        Viagem viagem = mock(Viagem.class);
        Usuario usuario = mock(Usuario.class);

        when(reservaSimuladaRepository.findById(10L))
                .thenReturn(Optional.of(reserva));

        when(reserva.getViagem()).thenReturn(viagem);
        when(viagem.getUsuario()).thenReturn(usuario);
        when(usuario.getId()).thenReturn(2L);

        when(reserva.getStatus())
                .thenReturn(StatusReservaSimulada.CONFIRMADA_EM_SIMULACAO);

        ReservaSimulada resultado =
                reservaSimuladaService.confirmar(10L);

        assertSame(reserva, resultado);

        verify(reservaSimuladaRepository, never())
                .save(any(ReservaSimulada.class));

        verifyNoInteractions(notificacaoService);
        verifyNoInteractions(eventoAuditoriaService);
    }
}