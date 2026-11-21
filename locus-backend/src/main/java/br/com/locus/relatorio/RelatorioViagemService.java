package br.com.locus.relatorio;

import br.com.locus.orcamento.OrcamentoService;
import br.com.locus.orcamento.ItemOrcamento;
import br.com.locus.orcamento.ItemOrcamentoRepository;
import br.com.locus.orcamento.dto.ResumoCategoriaOrcamento;
import br.com.locus.orcamento.dto.ResumoOrcamento;
import br.com.locus.reserva.ReservaSimulada;
import br.com.locus.reserva.ReservaSimuladaRepository;
import br.com.locus.reserva.StatusReservaSimulada;
import br.com.locus.roteiro.DiaRoteiro;
import br.com.locus.roteiro.DiaRoteiroRepository;
import br.com.locus.roteiro.ItemRoteiro;
import br.com.locus.roteiro.ItemRoteiroRepository;
import br.com.locus.transporte.EstimativaRota;
import br.com.locus.transporte.EstimativaRotaRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.locus.notificacao.NotificacaoService;
import br.com.locus.notificacao.TipoNotificacao;
import br.com.locus.notificacao.dto.NotificacaoRequest;
import br.com.locus.auditoria.EventoAuditoriaService;
import br.com.locus.auditoria.ResultadoAuditoria;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import br.com.locus.admin.ControleAcessoService;
import java.util.List;

@Service
public class RelatorioViagemService {

    private final RelatorioViagemRepository relatorioViagemRepository;
    private final ReservaSimuladaRepository reservaSimuladaRepository;
    private final OrcamentoService orcamentoService;
    private final ItemOrcamentoRepository itemOrcamentoRepository;
    private final EstimativaRotaRepository estimativaRotaRepository;
    private final DiaRoteiroRepository diaRoteiroRepository;
    private final ItemRoteiroRepository itemRoteiroRepository;
    private final NotificacaoService notificacaoService;
    private final EventoAuditoriaService eventoAuditoriaService;
    private final ControleAcessoService controleAcessoService;

    public RelatorioViagemService(
            RelatorioViagemRepository relatorioViagemRepository,
            ReservaSimuladaRepository reservaSimuladaRepository,
            OrcamentoService orcamentoService,
            ItemOrcamentoRepository itemOrcamentoRepository,
            EstimativaRotaRepository estimativaRotaRepository,
            DiaRoteiroRepository diaRoteiroRepository,
            ItemRoteiroRepository itemRoteiroRepository,
            NotificacaoService notificacaoService,
            EventoAuditoriaService eventoAuditoriaService,
            ControleAcessoService controleAcessoService) {

        this.relatorioViagemRepository = relatorioViagemRepository;
        this.reservaSimuladaRepository = reservaSimuladaRepository;
        this.orcamentoService = orcamentoService;
        this.itemOrcamentoRepository = itemOrcamentoRepository;
        this.estimativaRotaRepository = estimativaRotaRepository;
        this.diaRoteiroRepository = diaRoteiroRepository;
        this.itemRoteiroRepository = itemRoteiroRepository;
        this.notificacaoService = notificacaoService;
        this.eventoAuditoriaService = eventoAuditoriaService;
        this.controleAcessoService = controleAcessoService;
    }

    @Transactional
    public RelatorioViagem gerar(Long reservaId) {

        Long usuarioId = null;

        try {

            ReservaSimulada reserva =
                    reservaSimuladaRepository.findById(reservaId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Reserva simulada não encontrada."
                                    ));

            usuarioId =
                    reserva.getViagem()
                            .getUsuario()
                            .getId();

            if (reserva.getStatus()
                    != StatusReservaSimulada.CONFIRMADA_EM_SIMULACAO) {

                throw new RuntimeException(
                        "O relatório só pode ser gerado para uma reserva confirmada em simulação."
                );
            }

            if (relatorioViagemRepository
                    .existsByReservaSimuladaId(reservaId)) {
                return relatorioViagemRepository
                        .findByReservaSimuladaId(reservaId)
                        .orElseThrow(() -> new RuntimeException(
                                "Relatório existente não pôde ser recuperado."
                        ));
            }

            byte[] pdf = gerarPdf(reserva);

            RelatorioViagem relatorio =
                    new RelatorioViagem();

            relatorio.setReservaSimulada(reserva);

            relatorio.setNomeArquivo(
                    "relatorio-" + reserva.getProtocolo() + ".pdf"
            );

            relatorio.setArquivoPdf(pdf);
            relatorio.setDataGeracao(LocalDateTime.now());

            RelatorioViagem relatorioSalvo =
                    relatorioViagemRepository.save(relatorio);

            NotificacaoRequest notificacao =
                    new NotificacaoRequest();

            notificacao.setTipo(
                    TipoNotificacao.RELATORIO
            );

            notificacao.setTitulo(
                    "Relatório de viagem disponível"
            );

            notificacao.setMensagem(
                    "O relatório da reserva simulada "
                            + reserva.getProtocolo()
                            + " foi gerado e está disponível."
            );

            notificacaoService.criar(
                    usuarioId,
                    notificacao
            );

            eventoAuditoriaService.registrar(
                    usuarioId,
                    "GERAR_RELATORIO_VIAGEM",
                    "RELATORIO",
                    relatorioSalvo.getId(),
                    ResultadoAuditoria.SUCESSO,
                    "Relatório gerado para a reserva "
                            + reserva.getProtocolo()
            );

            return relatorioSalvo;

        } catch (RuntimeException e) {

            try {

                eventoAuditoriaService.registrarFalha(
                        usuarioId,
                        "GERAR_RELATORIO_VIAGEM",
                        "RESERVA",
                        reservaId,
                        e.getMessage()
                );

            } catch (RuntimeException erroAuditoria) {
                // Mantém o erro original da operação.
            }

            throw e;
        }
    }

    public RelatorioViagem buscarPorId(Long relatorioId) {

        return relatorioViagemRepository.findById(relatorioId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Relatório não encontrado."
                        ));
    }

    public RelatorioViagem buscarPorReserva(Long reservaId) {

        return relatorioViagemRepository
                .findByReservaSimuladaId(reservaId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Relatório não encontrado para esta reserva."
                        ));
    }

    public RelatorioViagem gerar(
            Long reservaId,
            Long usuarioSolicitanteId) {

        ReservaSimulada reserva =
                reservaSimuladaRepository.findById(reservaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reserva simulada não encontrada."
                                ));

        controleAcessoService.exigirProprietarioViagem(
                reserva.getViagem().getId(),
                usuarioSolicitanteId
        );

        return gerar(reservaId);
    }

    public RelatorioViagem buscarPorId(
            Long relatorioId,
            Long usuarioSolicitanteId) {

        RelatorioViagem relatorio =
                buscarPorId(relatorioId);

        controleAcessoService.exigirProprietarioViagem(
                relatorio.getReservaSimulada()
                        .getViagem()
                        .getId(),
                usuarioSolicitanteId
        );

        return relatorio;
    }

    public RelatorioViagem buscarPorReserva(
            Long reservaId,
            Long usuarioSolicitanteId) {

        ReservaSimulada reserva =
                reservaSimuladaRepository.findById(reservaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reserva simulada não encontrada."
                                ));

        controleAcessoService.exigirProprietarioViagem(
                reserva.getViagem().getId(),
                usuarioSolicitanteId
        );

        return buscarPorReserva(reservaId);
    }

    private byte[] gerarPdf(ReservaSimulada reserva) {

        Long viagemId = reserva.getViagem().getId();

        ResumoOrcamento resumoOrcamento =
                orcamentoService.gerarResumo(viagemId);

        EstimativaRota estimativaAtual =
                estimativaRotaRepository
                        .findFirstByViagemIdOrderByDataConsultaDesc(viagemId)
                        .orElse(null);

        List<DiaRoteiro> diasRoteiro =
                diaRoteiroRepository.findByViagemId(viagemId);

        List<ItemOrcamento> itensOrcamento =
                itemOrcamentoRepository.findByViagemId(viagemId);

        try (
                PDDocument documento = new PDDocument();
                ByteArrayOutputStream saida =
                        new ByteArrayOutputStream()
        ) {

            PDType1Font fonteNormal =
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA
                    );

            PDType1Font fonteNegrito =
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA_BOLD
                    );

            criarPaginaResumo(
                    documento,
                    reserva,
                    resumoOrcamento,
                    itensOrcamento,
                    estimativaAtual,
                    fonteNormal,
                    fonteNegrito
            );

            criarPaginasRoteiro(
                    documento,
                    diasRoteiro,
                    fonteNormal,
                    fonteNegrito
            );

            documento.save(saida);

            return saida.toByteArray();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao gerar o relatório em PDF."
            );
        }
    }

    private void criarPaginaResumo(
            PDDocument documento,
            ReservaSimulada reserva,
            ResumoOrcamento resumoOrcamento,
            List<ItemOrcamento> itensOrcamento,
            EstimativaRota estimativaAtual,
            PDType1Font fonteNormal,
            PDType1Font fonteNegrito) throws IOException {

        PDPage pagina = new PDPage();
        documento.addPage(pagina);

        try (PDPageContentStream conteudo =
                     new PDPageContentStream(
                             documento,
                             pagina
                     )) {

            float x = 50;
            float y = 735;

            desenharLogo(conteudo, x, 785, fonteNormal, fonteNegrito);

            escreverTexto(
                    conteudo,
                    fonteNegrito,
                    18,
                    "Seu roteiro de viagem",
                    x,
                    y - 5
            );

            y -= 40;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Protocolo:",
                    reserva.getProtocolo(),
                    x,
                    y
            );

            y -= 25;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Cliente:",
                    reserva.getViagem()
                            .getUsuario()
                            .getNome(),
                    x,
                    y
            );

            y -= 25;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Viagem:",
                    reserva.getViagem().getTitulo(),
                    x,
                    y
            );

            y -= 25;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Origem:",
                    reserva.getViagem().getOrigem(),
                    x,
                    y
            );

            y -= 25;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Destino:",
                    reserva.getViagem().getDestino(),
                    x,
                    y
            );

            y -= 25;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Periodo:",
                    reserva.getViagem().getDataInicio()
                            + " ate "
                            + reserva.getViagem().getDataFim(),
                    x,
                    y
            );

            y -= 25;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Modo de transporte:",
                    reserva.getViagem()
                            .getModoTransporte()
                            .toString(),
                    x,
                    y
            );

            y -= 25;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Valor total simulado:",
                    formatarDinheiro(
                            reserva.getValorTotalSimulado()
                    ),
                    x,
                    y
            );

            y -= 25;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Status da simulação:",
                    String.valueOf(reserva.getStatus()),
                    x,
                    y
            );

            y -= 25;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Confirmada em:",
                    String.valueOf(reserva.getDataConfirmacao()),
                    x,
                    y
            );

            y -= 40;

            escreverTexto(
                    conteudo,
                    fonteNegrito,
                    14,
                    "Resumo do Orcamento",
                    x,
                    y
            );

            y -= 25;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Tipo:",
                    resumoOrcamento
                            .getTipoOrcamento()
                            .toString(),
                    x,
                    y
            );

            y -= 20;

            escreverLinha(
                    conteudo,
                    fonteNegrito,
                    "Total:",
                    formatarDinheiro(
                            resumoOrcamento.getTotalGasto()
                    ),
                    x,
                    y
            );

            y -= 25;

            for (ResumoCategoriaOrcamento categoria :
                    resumoOrcamento.getCategorias()) {

                if (categoria.getSubtotal()
                        .compareTo(BigDecimal.ZERO) > 0
                        || categoria.getLimite() != null) {

                    String textoCategoria =
                            categoria.getCategoria()
                                    + ": "
                                    + formatarDinheiro(
                                    categoria.getSubtotal()
                            );

                    if (categoria.getLimite() != null) {

                        textoCategoria +=
                                " | Limite: "
                                        + formatarDinheiro(
                                        categoria.getLimite()
                                );
                    }

                    escreverTexto(
                            conteudo,
                            fonteNormal,
                            10,
                            textoCategoria,
                            x + 15,
                            y
                    );

                    y -= 18;
                }
            }

            y -= 10;

            escreverTexto(
                    conteudo,
                    fonteNegrito,
                    12,
                    "Itens considerados no planejamento",
                    x,
                    y
            );

            y -= 20;

            if (itensOrcamento.isEmpty()) {
                escreverTexto(
                        conteudo,
                        fonteNormal,
                        10,
                        "Nenhum item de orçamento foi registrado.",
                        x,
                        y
                );
                y -= 18;
            } else {
                for (ItemOrcamento item : itensOrcamento) {
                    escreverTexto(
                            conteudo,
                            fonteNormal,
                            9,
                            item.getCategoria()
                                    + " - "
                                    + item.getDescricao()
                                    + ": "
                                    + formatarDinheiro(item.getValor())
                                    + " | Fonte: "
                                    + item.getFonte(),
                            x + 15,
                            y
                    );
                    y -= 16;
                }
            }

            y -= 20;

            escreverTexto(
                    conteudo,
                    fonteNegrito,
                    14,
                    "Estimativa de Transporte",
                    x,
                    y
            );

            y -= 25;

            if (estimativaAtual == null) {

                escreverTexto(
                        conteudo,
                        fonteNormal,
                        10,
                        "Nenhuma estimativa de transporte cadastrada.",
                        x,
                        y
                );

                y -= 20;

            } else {

                escreverTexto(
                        conteudo,
                        fonteNormal,
                        10,
                        estimativaAtual.getModoTransporte()
                                + " - Distancia: "
                                + estimativaAtual.getDistanciaKm()
                                + " km",
                        x,
                        y
                );

                y -= 18;

                escreverTexto(
                        conteudo,
                        fonteNormal,
                        10,
                        "Duracao estimada: "
                                + estimativaAtual.getDuracaoMinutos()
                                + " minutos",
                        x + 15,
                        y
                );

                y -= 18;

                escreverTexto(
                        conteudo,
                        fonteNormal,
                        10,
                        "Custo total estimado: "
                                + formatarDinheiro(
                                estimativaAtual.getCustoTotalEstimado()
                        ),
                        x + 15,
                        y
                );

                y -= 18;

                escreverTexto(
                        conteudo,
                        fonteNormal,
                        10,
                        "Fonte: "
                                + estimativaAtual.getFonteEstimativa(),
                        x + 15,
                        y
                );

                y -= 18;

                escreverTexto(
                        conteudo,
                        fonteNormal,
                        10,
                        "Consultado em: "
                                + estimativaAtual.getDataConsulta(),
                        x + 15,
                        y
                );

                y -= 25;
            }

            escreverTexto(
                    conteudo,
                    fonteNegrito,
                    12,
                    "AVISO IMPORTANTE",
                    x,
                    y
            );

            y -= 25;

            escreverTexto(
                    conteudo,
                    fonteNormal,
                    10,
                    "Este documento representa apenas uma simulacao academica.",
                    x,
                    y
            );

            y -= 18;

            escreverTexto(
                    conteudo,
                    fonteNormal,
                    10,
                    "Nenhum pagamento real, emissao de passagem ou reserva foi realizado.",
                    x,
                    y
            );

            y -= 18;

            escreverTexto(
                    conteudo,
                    fonteNormal,
                    10,
                    "O Locus nao realizou contato com fornecedores externos.",
                    x,
                    y
            );
        }
    }

    private void criarPaginasRoteiro(
            PDDocument documento,
            List<DiaRoteiro> diasRoteiro,
            PDType1Font fonteNormal,
            PDType1Font fonteNegrito) throws IOException {

        if (diasRoteiro.isEmpty()) {

            PDPage pagina = new PDPage();
            documento.addPage(pagina);

            try (PDPageContentStream conteudo =
                         new PDPageContentStream(
                                 documento,
                                 pagina
                         )) {

                desenharLogo(conteudo, 50, 785, fonteNormal, fonteNegrito);

                escreverTexto(
                        conteudo,
                        fonteNegrito,
                        16,
                        "Roteiro da Viagem",
                        50,
                        735
                );

                escreverTexto(
                        conteudo,
                        fonteNormal,
                        11,
                        "Nenhum roteiro foi cadastrado para esta viagem.",
                        50,
                        710
                );
            }

            return;
        }

        for (DiaRoteiro dia : diasRoteiro) {

            PDPage pagina = new PDPage();
            documento.addPage(pagina);

            try (PDPageContentStream conteudo =
                         new PDPageContentStream(
                                 documento,
                                 pagina
                         )) {

                float x = 50;
                float y = 735;

                desenharLogo(conteudo, x, 785, fonteNormal, fonteNegrito);

                escreverTexto(
                        conteudo,
                        fonteNegrito,
                        16,
                        "Roteiro - " + dia.getData(),
                        x,
                        y
                );

                y -= 30;

                if (dia.getObservacoes() != null
                        && !dia.getObservacoes().isBlank()) {

                    escreverTexto(
                            conteudo,
                            fonteNormal,
                            10,
                            "Observacoes: "
                                    + dia.getObservacoes(),
                            x,
                            y
                    );

                    y -= 30;
                }

                List<ItemRoteiro> itens =
                        itemRoteiroRepository
                                .findByDiaRoteiroIdOrderByOrdemAsc(
                                        dia.getId()
                                );

                if (itens.isEmpty()) {

                    escreverTexto(
                            conteudo,
                            fonteNormal,
                            11,
                            "Nenhuma atividade cadastrada para este dia.",
                            x,
                            y
                    );

                } else {

                    for (ItemRoteiro item : itens) {

                        escreverTexto(
                                conteudo,
                                fonteNegrito,
                                11,
                                item.getOrdem()
                                        + ". "
                                        + item.getTitulo(),
                                x,
                                y
                        );

                        y -= 20;

                        escreverTexto(
                                conteudo,
                                fonteNormal,
                                10,
                                "Horario: "
                                        + item.getHorarioInicio()
                                        + " | Duracao: "
                                        + item.getDuracaoMinutos()
                                        + " minutos",
                                x + 15,
                                y
                        );

                        y -= 18;

                        if (item.getLugar() != null) {

                            escreverTexto(
                                    conteudo,
                                    fonteNormal,
                                    10,
                                    "Lugar: "
                                            + item.getLugar()
                                            .getNome(),
                                    x + 15,
                                    y
                            );

                            y -= 18;
                        }

                        if (item.getCustoEstimado() != null) {

                            escreverTexto(
                                    conteudo,
                                    fonteNormal,
                                    10,
                                    "Custo estimado: R$ "
                                            + item.getCustoEstimado(),
                                    x + 15,
                                    y
                            );

                            y -= 18;
                        }

                        if (item.getMotivoRecomendacao() != null
                                && !item.getMotivoRecomendacao()
                                .isBlank()) {

                            escreverTexto(
                                    conteudo,
                                    fonteNormal,
                                    10,
                                    "Motivo: "
                                            + item
                                            .getMotivoRecomendacao(),
                                    x + 15,
                                    y
                            );

                            y -= 18;
                        }

                        y -= 15;
                    }
                }
            }
        }
    }

    private void escreverLinha(
            PDPageContentStream conteudo,
            PDType1Font fonteNegrito,
            String titulo,
            String valor,
            float x,
            float y) throws IOException {

        escreverTexto(
                conteudo,
                fonteNegrito,
                11,
                titulo + " " + valor,
                x,
                y
        );
    }

    private void escreverTexto(
            PDPageContentStream conteudo,
            PDType1Font fonte,
            float tamanho,
            String texto,
            float x,
            float y) throws IOException {

        conteudo.beginText();
        conteudo.setFont(fonte, tamanho);
        conteudo.newLineAtOffset(x, y);
        conteudo.showText(normalizarPdf(texto));
        conteudo.endText();
    }

    private void desenharLogo(
            PDPageContentStream conteudo,
            float x,
            float y,
            PDType1Font fonteNormal,
            PDType1Font fonteNegrito) throws IOException {

        conteudo.setNonStrokingColor(
                new PDColor(new float[]{0.91f, 0.47f, 0.29f}, PDDeviceRGB.INSTANCE)
        );
        conteudo.moveTo(x + 14, y + 5);
        conteudo.curveTo(x + 2, y + 20, x - 2, y - 2, x + 14, y - 17);
        conteudo.curveTo(x + 30, y - 2, x + 26, y + 20, x + 14, y + 5);
        conteudo.closePath();
        conteudo.fill();
        conteudo.setNonStrokingColor(
                new PDColor(new float[]{0.99f, 0.97f, 0.94f}, PDDeviceRGB.INSTANCE)
        );
        float centroX = x + 14;
        float centroY = y + 3;
        float raio = 7;
        float controle = raio * 0.5522848f;
        conteudo.moveTo(centroX + raio, centroY);
        conteudo.curveTo(centroX + raio, centroY + controle, centroX + controle, centroY + raio, centroX, centroY + raio);
        conteudo.curveTo(centroX - controle, centroY + raio, centroX - raio, centroY + controle, centroX - raio, centroY);
        conteudo.curveTo(centroX - raio, centroY - controle, centroX - controle, centroY - raio, centroX, centroY - raio);
        conteudo.curveTo(centroX + controle, centroY - raio, centroX + raio, centroY - controle, centroX + raio, centroY);
        conteudo.closePath();
        conteudo.fill();

        conteudo.setNonStrokingColor(
                new PDColor(new float[]{0.46f, 0.40f, 0.66f}, PDDeviceRGB.INSTANCE)
        );
        conteudo.moveTo(centroX, centroY - 6);
        conteudo.lineTo(centroX + 2.2f, centroY - 2.2f);
        conteudo.lineTo(centroX + 6, centroY);
        conteudo.lineTo(centroX + 2.2f, centroY + 2.2f);
        conteudo.lineTo(centroX, centroY + 6);
        conteudo.lineTo(centroX - 2.2f, centroY + 2.2f);
        conteudo.lineTo(centroX - 6, centroY);
        conteudo.lineTo(centroX - 2.2f, centroY - 2.2f);
        conteudo.closePath();
        conteudo.fill();

        escreverTexto(
                conteudo,
                fonteNegrito,
                24,
                "Locus",
                x + 38,
                y + 1
        );

        escreverTexto(
                conteudo,
                fonteNormal,
                7,
                "Planeje viagens com inteligencia",
                x + 38,
                y - 13
        );

        conteudo.setNonStrokingColor(
                new PDColor(new float[]{0.44f, 0.65f, 0.54f}, PDDeviceRGB.INSTANCE)
        );
        conteudo.setLineWidth(1.2f);
        conteudo.moveTo(x + 39, y - 19);
        conteudo.lineTo(x + 103, y - 19);
        conteudo.stroke();

        conteudo.setNonStrokingColor(
                new PDColor(new float[]{0.15f, 0.20f, 0.23f}, PDDeviceRGB.INSTANCE)
        );
    }

    private String normalizarPdf(String texto) {
        if (texto == null) {
            return "";
        }

        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^\\x20-\\x7E]", "?");
    }

    private String formatarDinheiro(BigDecimal valor) {

        if (valor == null) {
            return "Nao informado";
        }

        return "R$ " + valor.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
