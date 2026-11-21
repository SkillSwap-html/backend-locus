package br.com.locus.viagem;

import br.com.locus.perfil.ModoTransporte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegraTransporteServiceTest {

    private RegraTransporteService regraTransporteService;

    @BeforeEach
    void prepararTeste() {
        regraTransporteService = new RegraTransporteService();
    }

    @Test
    void devePermitirTransporteTerrestreQuandoEstiverDisponivel() {
        assertDoesNotThrow(() ->
                regraTransporteService.validar(
                        ModoTransporte.CARRO,
                        ViabilidadeTransporteTerrestre.DISPONIVEL
                )
        );
    }

    @Test
    void deveNegarTransporteTerrestreQuandoEstiverIndisponivel() {
        assertThrows(
                RuntimeException.class,
                () -> regraTransporteService.validar(
                        ModoTransporte.CARRO,
                        ViabilidadeTransporteTerrestre.INDISPONIVEL
                )
        );
    }
}