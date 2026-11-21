package br.com.locus.lugar;

import br.com.locus.admin.ControleAcessoService;
import br.com.locus.compartilhamento.PermissaoCompartilhamentoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FonteLugarDetalheService {

    private final FonteLugarDetalheRepository fonteLugarDetalheRepository;
    private final LugarRepository lugarRepository;
    private final ControleAcessoService controleAcessoService;
    private final PermissaoCompartilhamentoService permissaoCompartilhamentoService;

    public FonteLugarDetalheService(
            FonteLugarDetalheRepository fonteLugarDetalheRepository,
            LugarRepository lugarRepository,
            ControleAcessoService controleAcessoService,
            PermissaoCompartilhamentoService permissaoCompartilhamentoService) {

        this.fonteLugarDetalheRepository = fonteLugarDetalheRepository;
        this.lugarRepository = lugarRepository;
        this.controleAcessoService = controleAcessoService;
        this.permissaoCompartilhamentoService = permissaoCompartilhamentoService;
    }

    public FonteLugarDetalhe salvar(
            Long lugarId,
            FonteLugarDetalhe fonteLugarDetalhe) {

        Lugar lugar = lugarRepository.findById(lugarId)
                .orElseThrow(() ->
                        new RuntimeException("Lugar não encontrado.")
                );

        if (fonteLugarDetalheRepository.existsByLugarId(lugarId)) {
            throw new RuntimeException(
                    "Este lugar já possui informações de fonte cadastradas."
            );
        }

        if (fonteLugarDetalhe.getFonte() == null) {
            throw new RuntimeException(
                    "A fonte é obrigatória."
            );
        }

        if (fonteLugarDetalhe.getDataConsulta() == null) {
            fonteLugarDetalhe.setDataConsulta(
                    LocalDateTime.now()
            );
        }

        fonteLugarDetalhe.setLugar(lugar);

        return fonteLugarDetalheRepository.save(
                fonteLugarDetalhe
        );
    }

    public FonteLugarDetalhe salvar(
            Long lugarId,
            Long usuarioSolicitanteId,
            FonteLugarDetalhe fonteLugarDetalhe) {

        controleAcessoService.exigirCuradorOuAdministrador(
                usuarioSolicitanteId
        );

        return salvar(
                lugarId,
                fonteLugarDetalhe
        );
    }

    public Optional<FonteLugarDetalhe> buscarPorLugar(
            Long lugarId) {

        return fonteLugarDetalheRepository.findByLugarId(
                lugarId
        );
    }

    public Optional<FonteLugarDetalhe> buscarPorLugar(
            Long lugarId,
            Long usuarioSolicitanteId) {

        Lugar lugar = lugarRepository.findById(lugarId)
                .orElseThrow(() ->
                        new RuntimeException("Lugar não encontrado.")
                );

        permissaoCompartilhamentoService.validarVisualizacao(
                lugar.getDestino().getViagem().getId(),
                usuarioSolicitanteId
        );

        return buscarPorLugar(lugarId);
    }
}