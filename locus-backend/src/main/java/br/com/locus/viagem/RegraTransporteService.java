package br.com.locus.viagem;

import br.com.locus.perfil.ModoTransporte;
import org.springframework.stereotype.Service;

@Service
public class RegraTransporteService {

    public void validar(
            ModoTransporte modoTransporte,
            ViabilidadeTransporteTerrestre viabilidade) {

        if (modoTransporte == null) {
            throw new RuntimeException(
                    "O modo de transporte é obrigatório."
            );
        }

        boolean transporteTerrestre =
                modoTransporte == ModoTransporte.CARRO
                        || modoTransporte == ModoTransporte.MOTOCICLETA
                        || modoTransporte == ModoTransporte.VEICULO_PARTICULAR
                        || modoTransporte == ModoTransporte.ONIBUS;

        if (transporteTerrestre
                && viabilidade
                == ViabilidadeTransporteTerrestre.INDISPONIVEL) {

            throw new RuntimeException(
                    "O modo de transporte escolhido não é viável para esta viagem."
            );
        }
    }
}