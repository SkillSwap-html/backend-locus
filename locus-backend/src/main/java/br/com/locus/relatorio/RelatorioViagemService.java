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
import br.com.locus.viagem.Viagem;
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
import java.text.NumberFormat;
import java.text.Normalizer;
import java.time.LocalDateTime;
import br.com.locus.admin.ControleAcessoService;
import java.util.List;
import java.util.Locale;

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
                RelatorioViagem existente = relatorioViagemRepository
                        .findByReservaSimuladaId(reservaId)
                        .orElseThrow(() -> new RuntimeException(
                                "Relatório existente não pôde ser recuperado."
                        ));
                String nomeArquivo = nomeArquivoRoteiro(reserva.getViagem());
                existente.setNomeArquivo(nomeArquivo);
                existente.setArquivoPdf(gerarPdf(reserva));
                existente.setDataGeracao(LocalDateTime.now());
                return relatorioViagemRepository.save(existente);
            }

            byte[] pdf = gerarPdf(reserva);

            RelatorioViagem relatorio =
                    new RelatorioViagem();

            relatorio.setReservaSimulada(reserva);

            relatorio.setNomeArquivo(
                    nomeArquivoRoteiro(reserva.getViagem())
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

        return relatorioViagemRepository
                .findByReservaSimuladaId(reservaId)
                .orElseGet(() -> {
                    if (reserva.getStatus()
                            != StatusReservaSimulada.CONFIRMADA_EM_SIMULACAO) {

                        throw new RuntimeException(
                                "O relatório só pode ser gerado após a confirmação da simulação."
                        );
                    }

                    // Compatibilidade com versões antigas do frontend que
                    // consultavam o relatório por GET antes de existir.
                    return gerar(reservaId);
                });
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

            criarPaginasItensOrcamento(
                    documento,
                    itensOrcamento,
                    fonteNormal,
                    fonteNegrito
            );

            criarPaginasRoteiro(
                    documento,
                    diasRoteiro,
                    itensOrcamento,
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
            // A logo ocupa a faixa superior; o título começa abaixo do
            // subtítulo para não haver sobreposição no PDF.
            float y = 715;

            desenharLogo(conteudo, x, 755, fonteNormal, fonteNegrito);

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
                            // O quadro do orçamento é recalculado com os itens
                            // atuais da viagem. O cabeçalho precisa usar a
                            // mesma fonte para não apresentar dois totais
                            // diferentes no mesmo PDF.
                            resumoOrcamento.getTotalGasto()
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

            y -= 18;

            desenharCartao(
                    conteudo,
                    x,
                    y - 58,
                    512,
                    58,
                    new PDColor(new float[]{0.97f, 0.95f, 0.91f}, PDDeviceRGB.INSTANCE)
            );
            escreverTexto(conteudo, fonteNegrito, 9, "TOTAL ACUMULADO", x + 14, y - 18);
            escreverTexto(
                    conteudo,
                    fonteNegrito,
                    18,
                    formatarDinheiro(resumoOrcamento.getTotalGasto()),
                    x + 14,
                    y - 41
            );
            escreverTexto(
                    conteudo,
                    fonteNormal,
                    8,
                    resumoOrcamento.getLimiteTotal() == null
                            ? "Orcamento aberto"
                            : "Limite total: " + formatarDinheiro(resumoOrcamento.getLimiteTotal()),
                    x + 355,
                    y - 39
            );

            y -= 76;

            float larguraCartao = 160;
            float alturaCartao = 58;
            float espacoCartao = 16;
            List<ResumoCategoriaOrcamento> categorias = resumoOrcamento.getCategorias();

            for (int indice = 0; indice < categorias.size(); indice++) {
                ResumoCategoriaOrcamento categoria = categorias.get(indice);
                int linha = indice / 3;
                int coluna = indice % 3;
                float cartaoX = x + coluna * (larguraCartao + espacoCartao);
                float cartaoY = y - (linha + 1) * alturaCartao - linha * 10;

                desenharCartao(
                        conteudo,
                        cartaoX,
                        cartaoY,
                        larguraCartao,
                        alturaCartao,
                        new PDColor(new float[]{0.995f, 0.99f, 0.96f}, PDDeviceRGB.INSTANCE)
                );
                escreverTexto(conteudo, fonteNegrito, 9, rotuloCategoria(categoria), cartaoX + 10, cartaoY + 40);
                escreverTexto(conteudo, fonteNegrito, 12, formatarDinheiro(categoria.getSubtotal()), cartaoX + 10, cartaoY + 23);
                escreverTexto(
                        conteudo,
                        fonteNormal,
                        7,
                        categoria.getLimite() == null
                                ? "Sem limite definido"
                                : "Limite " + formatarDinheiro(categoria.getLimite()),
                        cartaoX + 10,
                        cartaoY + 9
                );
            }

            y -= 144;

            escreverTexto(
                    conteudo,
                    fonteNegrito,
                    12,
                    "Itens considerados no planejamento",
                    x,
                    y
            );

            y -= 20;

            escreverTextoQuebrado(
                    conteudo,
                    fonteNormal,
                    10,
                    itensOrcamento.isEmpty()
                            ? "Nenhum item de orçamento foi registrado."
                            : "Os itens de orçamento estão detalhados em uma página própria deste relatório.",
                    x,
                    y,
                    512,
                    15
            );

            y -= 38;

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
            List<ItemOrcamento> itensOrcamento,
            PDType1Font fonteNormal,
            PDType1Font fonteNegrito) throws IOException {
        if (diasRoteiro.isEmpty()) {
            PDPage pagina = new PDPage();
            documento.addPage(pagina);
            try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {
                desenharLogo(conteudo, 50, 750, fonteNormal, fonteNegrito);
                escreverTexto(conteudo, fonteNegrito, 16, "Roteiro da Viagem", 50, 710);
                escreverTexto(conteudo, fonteNormal, 11,
                        "Nenhum roteiro foi cadastrado para esta viagem.", 50, 680);
            }
            return;
        }

        for (DiaRoteiro dia : diasRoteiro) {
            List<ItemRoteiro> itens = itemRoteiroRepository
                    .findByDiaRoteiroIdOrderByOrdemAsc(dia.getId());
            int indiceItem = 0;
            boolean primeiraPagina = true;

            do {
                PDPage pagina = new PDPage();
                documento.addPage(pagina);

                try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {
                    float x = 50;
                    float y = 710;
                    desenharLogo(conteudo, x, 750, fonteNormal, fonteNegrito);

                    escreverTexto(
                            conteudo,
                            fonteNegrito,
                            16,
                            "Roteiro - " + dia.getData() + (primeiraPagina ? "" : " (continuacao)"),
                            x,
                            y
                    );
                    y -= 30;

                    if (primeiraPagina && dia.getObservacoes() != null
                            && !dia.getObservacoes().isBlank()) {
                        y = escreverTextoQuebrado(
                                conteudo,
                                fonteNormal,
                                10,
                                "Observacoes: " + dia.getObservacoes(),
                                x,
                                y,
                                512,
                                15
                        ) - 12;
                    }

                    if (itens.isEmpty()) {
                        escreverTexto(conteudo, fonteNormal, 11,
                                "Nenhuma atividade cadastrada para este dia.", x, y);
                    } else {
                        while (indiceItem < itens.size()) {
                            ItemRoteiro item = itens.get(indiceItem);
                            List<ItemOrcamento> custosDoLocal = itensOrcamento.stream()
                                    .filter(custo -> custo.getDescricao() != null
                                            && (custo.getDescricao().equals(item.getTitulo())
                                            || custo.getDescricao().startsWith(item.getTitulo() + " -")))
                                    .toList();
                            String motivo = limparMotivo(item.getMotivoRecomendacao());
                            float altura = alturaItemRoteiro(item, custosDoLocal, motivo,
                                    fonteNormal, fonteNegrito);

                            if (indiceItem > 0 && y - altura < 55) {
                                break;
                            }

                            y = escreverTextoQuebrado(
                                    conteudo,
                                    fonteNegrito,
                                    11,
                                    item.getOrdem() + ". " + item.getTitulo(),
                                    x,
                                    y,
                                    512,
                                    16
                            ) - 4;
                            y = escreverTextoQuebrado(
                                    conteudo,
                                    fonteNormal,
                                    10,
                                    "Horario: " + item.getHorarioInicio()
                                            + " | Duracao: " + item.getDuracaoMinutos() + " minutos",
                                    x + 15,
                                    y,
                                    497,
                                    15
                            ) - 3;

                            if (item.getLugar() != null) {
                                y = escreverTextoQuebrado(conteudo, fonteNormal, 10,
                                        "Lugar: " + item.getLugar().getNome(), x + 15, y, 497, 15) - 3;
                            }
                            if (item.getCustoEstimado() != null) {
                                y = escreverTextoQuebrado(conteudo, fonteNormal, 10,
                                        "Custo estimado: " + formatarDinheiro(
                                                BigDecimal.valueOf(item.getCustoEstimado())),
                                        x + 15, y, 497, 15) - 3;
                            }
                            for (ItemOrcamento custo : custosDoLocal) {
                                String rotulo = switch (custo.getCategoria()) {
                                    case VISITAS -> "Visita";
                                    case ALIMENTACAO -> "Alimentacao";
                                    case TRANSPORTE_LOCAL -> "Transporte local";
                                    case EXTRAS -> "Extras";
                                    default -> null;
                                };
                                if (rotulo == null) continue;
                                y = escreverTextoQuebrado(conteudo, fonteNormal, 10,
                                        rotulo + " estimado: " + formatarDinheiro(custo.getValor()),
                                        x + 15, y, 497, 15) - 3;
                            }
                            if (!motivo.isBlank()) {
                                y = escreverTextoQuebrado(conteudo, fonteNormal, 10,
                                        "Motivo: " + motivo, x + 15, y, 497, 15);
                            }
                            y -= 12;
                            indiceItem++;
                        }
                    }
                }
                primeiraPagina = false;
            } while (indiceItem < itens.size());
        }
    }

    private void criarPaginasItensOrcamento(
            PDDocument documento,
            List<ItemOrcamento> itensOrcamento,
            PDType1Font fonteNormal,
            PDType1Font fonteNegrito) throws IOException {

        if (itensOrcamento.isEmpty()) {
            return;
        }

        int indiceItem = 0;
        boolean primeiraPagina = true;
        do {
            PDPage pagina = new PDPage();
            documento.addPage(pagina);
            try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {
                float x = 50;
                float y = 710;
                desenharLogo(conteudo, x, 750, fonteNormal, fonteNegrito);
                escreverTexto(
                        conteudo,
                        fonteNegrito,
                        16,
                        primeiraPagina
                                ? "Itens do orcamento"
                                : "Itens do orcamento (continuacao)",
                        x,
                        y
                );
                y -= 30;

                while (indiceItem < itensOrcamento.size()) {
                    ItemOrcamento item = itensOrcamento.get(indiceItem);
                    String texto = item.getCategoria()
                            + " - " + item.getDescricao()
                            + ": " + formatarDinheiro(item.getValor())
                            + " | Fonte: " + item.getFonte();
                    float altura = alturaTexto(fonteNormal, 9, texto, 482, 14) + 8;
                    if (indiceItem > 0 && y - altura < 55) {
                        break;
                    }
                    y = escreverTextoQuebrado(
                            conteudo,
                            fonteNormal,
                            9,
                            texto,
                            x + 15,
                            y,
                            482,
                            14
                    ) - 8;
                    indiceItem++;
                }
            }
            primeiraPagina = false;
        } while (indiceItem < itensOrcamento.size());
    }

    private float alturaItemRoteiro(
            ItemRoteiro item,
            List<ItemOrcamento> custosDoLocal,
            String motivo,
            PDType1Font fonteNormal,
            PDType1Font fonteNegrito) throws IOException {

        float altura = alturaTexto(
                fonteNegrito,
                11,
                item.getOrdem() + ". " + item.getTitulo(),
                512,
                16
        ) + 4;
        altura += alturaTexto(
                fonteNormal,
                10,
                "Horario: " + item.getHorarioInicio()
                        + " | Duracao: " + item.getDuracaoMinutos() + " minutos",
                497,
                15
        ) + 3;
        if (item.getLugar() != null) {
            altura += alturaTexto(fonteNormal, 10,
                    "Lugar: " + item.getLugar().getNome(), 497, 15) + 3;
        }
        if (item.getCustoEstimado() != null) {
            altura += alturaTexto(fonteNormal, 10,
                    "Custo estimado: " + formatarDinheiro(
                            BigDecimal.valueOf(item.getCustoEstimado())),
                    497, 15) + 3;
        }
        for (ItemOrcamento custo : custosDoLocal) {
            String rotulo = switch (custo.getCategoria()) {
                case VISITAS -> "Visita";
                case ALIMENTACAO -> "Alimentacao";
                case TRANSPORTE_LOCAL -> "Transporte local";
                case EXTRAS -> "Extras";
                default -> null;
            };
            if (rotulo != null) {
                altura += alturaTexto(fonteNormal, 10,
                        rotulo + " estimado: " + formatarDinheiro(custo.getValor()),
                        497, 15) + 3;
            }
        }
        if (!motivo.isBlank()) {
            altura += alturaTexto(fonteNormal, 10,
                    "Motivo: " + motivo, 497, 15);
        }
        return altura + 12;
    }

    private float escreverTextoQuebrado(
            PDPageContentStream conteudo,
            PDType1Font fonte,
            float tamanho,
            String texto,
            float x,
            float y,
            float larguraMaxima,
            float espacamentoLinha) throws IOException {

        for (String linha : quebrarLinhas(fonte, tamanho, texto, larguraMaxima)) {
            escreverTexto(conteudo, fonte, tamanho, linha, x, y);
            y -= espacamentoLinha;
        }
        return y;
    }

    private float alturaTexto(
            PDType1Font fonte,
            float tamanho,
            String texto,
            float larguraMaxima,
            float espacamentoLinha) throws IOException {

        return quebrarLinhas(fonte, tamanho, texto, larguraMaxima).size()
                * espacamentoLinha;
    }

    private List<String> quebrarLinhas(
            PDType1Font fonte,
            float tamanho,
            String texto,
            float larguraMaxima) throws IOException {

        String normalizado = normalizarPdf(texto == null ? "" : texto)
                .replace("\r", "");
        List<String> linhas = new java.util.ArrayList<>();
        for (String paragrafo : normalizado.split("\n", -1)) {
            String[] palavras = paragrafo.trim().split("\\s+");
            if (paragrafo.isBlank()) {
                linhas.add("");
                continue;
            }
            String linhaAtual = "";
            for (String palavra : palavras) {
                String candidata = linhaAtual.isBlank()
                        ? palavra
                        : linhaAtual + " " + palavra;
                float largura = fonte.getStringWidth(candidata) / 1000f * tamanho;
                if (linhaAtual.isBlank() || largura <= larguraMaxima) {
                    linhaAtual = candidata;
                } else {
                    linhas.add(linhaAtual);
                    linhaAtual = palavra;
                }
            }
            if (!linhaAtual.isBlank()) {
                linhas.add(linhaAtual);
            }
        }
        return linhas.isEmpty() ? List.of("") : linhas;
    }

    private String limparMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            return "";
        }
        return motivo.trim()
                .replaceAll(
                        "(?i)Encontrado no Google Places\\s*[.;]?\\s*",
                        ""
                )
                .replaceAll(
                        "(?i)(Confira o Street View antes de adicionar\\.\\s*)+",
                        ""
                )
                .replaceAll(
                        "(?i)O valor [eé] uma estimativa e ser[aá] somado somente se voc[eê] adicionar o local\\.?",
                        ""
                )
                .replaceAll("\\s{2,}", " ")
                .replaceAll("\\s+([.,;])", "$1")
                .trim();
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

    private void desenharCartao(
            PDPageContentStream conteudo,
            float x,
            float y,
            float largura,
            float altura,
            PDColor fundo) throws IOException {

        conteudo.setNonStrokingColor(fundo);
        conteudo.addRect(x, y, largura, altura);
        conteudo.fill();

        conteudo.setStrokingColor(
                new PDColor(new float[]{0.88f, 0.85f, 0.79f}, PDDeviceRGB.INSTANCE)
        );
        conteudo.setLineWidth(0.7f);
        conteudo.addRect(x, y, largura, altura);
        conteudo.stroke();

        conteudo.setNonStrokingColor(
                new PDColor(new float[]{0.08f, 0.23f, 0.24f}, PDDeviceRGB.INSTANCE)
        );
    }

    private String rotuloCategoria(ResumoCategoriaOrcamento categoria) {

        return switch (categoria.getCategoria()) {
            case PASSAGEM -> "Passagem";
            case HOSPEDAGEM -> "Hospedagem";
            case VISITAS -> "Visitas";
            case ALIMENTACAO -> "Alimentacao";
            case TRANSPORTE_LOCAL -> "Transporte local";
            case EXTRAS -> "Extras";
        };
    }

    private String nomeArquivoRoteiro(Viagem viagem) {

        String titulo = viagem.getTitulo();
        if (titulo == null || titulo.isBlank()) {
            titulo = "Roteiro para " + viagem.getDestino();
        }

        String slug = Normalizer.normalize(titulo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]+", "-")
                .replaceAll("^-+|-+$", "")
                .toLowerCase(Locale.ROOT);

        if (slug.isBlank()) {
            slug = "locus";
        }

        return "roteiro-" + slug + ".pdf";
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

        NumberFormat formato = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);
        return "R$ " + formato.format(valor);
    }
}
