package br.com.locus.integracao;

import br.com.locus.integracao.google.GooglePlacesClient;
import br.com.locus.integracao.google.GoogleRoutesClient;
import br.com.locus.integracao.google.GoogleStreetViewClient;
import br.com.locus.integracao.ofertas.OfertasSimuladasClient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/integracoes")
public class IntegracoesController {

    private final AssistenteViagemClient assistenteViagemClient;
    private final GooglePlacesClient placesClient;
    private final GoogleRoutesClient routesClient;
    private final GoogleStreetViewClient streetViewClient;
    private final OfertasSimuladasClient ofertasSimuladasClient;

    public IntegracoesController(
            AssistenteViagemClient assistenteViagemClient,
            GooglePlacesClient placesClient,
            GoogleRoutesClient routesClient,
            GoogleStreetViewClient streetViewClient,
            OfertasSimuladasClient ofertasSimuladasClient) {

        this.assistenteViagemClient = assistenteViagemClient;
        this.placesClient = placesClient;
        this.routesClient = routesClient;
        this.streetViewClient = streetViewClient;
        this.ofertasSimuladasClient = ofertasSimuladasClient;
    }

    @PostMapping("/gemini/conteudo")
    public JsonNode gerarConteudo(
            @Valid @RequestBody GeminiRequest request) {

        return assistenteViagemClient.gerar(
                "Você é o assistente de planejamento do Locus. "
                        + "Responda exclusivamente com um objeto JSON válido, sem markdown, sem cercas de código e sem texto fora do JSON. "
                        + "Use exatamente este formato: "
                        + "{\"resposta\":\"texto em português\",\"etapa\":\"passagem|hospedagem|roteiro|geral\","
                        + "\"consultas\":[\"pontos turísticos, praias e atrações na cidade e país do destino\"],"
                        + "\"orcamentoSugerido\":{\"passagem\":0,\"hospedagem\":0,\"visitas\":0,\"alimentacao\":0,\"transporteLocal\":0,\"extras\":0},"
                        + "\"recomendacoes\":[{\"id\":\"id único\",\"tipo\":\"lugar|atividade|hospedagem|voo\","
                        + "\"titulo\":\"nome oficial real\",\"endereco\":\"endereço completo real com cidade e país\",\"resumo\":\"descrição baseada em dados\","
                        + "\"justificativa\":\"por que combina com o pedido\",\"consulta\":\"nome oficial, endereço completo, cidade e país\","
                        + "\"valor\":0,\"categoria\":\"passagem|hospedagem|visitas|alimentacao|transporteLocal|extras\","
                        + "\"natureza\":\"observado|estimado|simulado\",\"evidencia\":{\"fonte\":\"fonte e URL quando houver\",\"consultadoEm\":\"data\"}}]} . "
                        + "Use no máximo 8 consultas e 8 recomendações, mantendo textos curtos para que a resposta permaneça válida em JSON. "
                        + "Conduza a conversa por etapas: primeiro passagem, depois hospedagem e só então roteiro de turismo. "
                        + "Se a pessoa já informou uma etapa, avance sem repetir perguntas. "
                        + "Se o destino tiver vários locais separados por ponto e vírgula ou barra vertical, trate cada cidade, estado e país separadamente e inclua consultas para cada um. Se a pessoa pedir pontos próximos, mas informar apenas o país ou estado sem cidade ou região, pergunte qual cidade ou região deve ser considerada antes de confirmar locais próximos. "
                        + "Em pedidos de roteiro, turismo ou recomendações de locais, nunca retorne somente hotéis: inclua consultas separadas para praias, restaurantes/gastronomia e pontos turísticos, museus, parques ou atrações. "
                        + "Só inclua hotéis ou hospedagens quando a pessoa pedir hospedagem ou quando a etapa atual for hospedagem. "
                        + "Quando ela pedir um lugar, praia, museu, restaurante ou roteiro, recomende locais reais dentro da cidade e país informados. Use a busca na web/Google Search quando disponível para confirmar o nome oficial e o endereço completo; o Google Places é opcional. "
                        + "Em cada recomendação de local, preencha obrigatoriamente titulo com o nome oficial real, endereco com rua/número/bairro/cidade/país quando essa informação estiver disponível, e consulta com nome + endereço para abrir o Maps. "
                        + "Em pedidos de pontos turísticos, praias, restaurantes ou roteiro, entregue as recomendações no próprio JSON do chat; não responda somente com uma lista de consultas. "
                        + "Nunca invente um endereço: se não conseguir confirmar o endereço completo, não apresente a recomendação como confirmada e explique isso na resposta. "
                        + "Nunca invente preços, avaliações, disponibilidade ou imagens; identifique claramente qualquer estimativa e informe fonte e data. "
                        + "Não repita saudações, perguntas ou respostas já dadas no histórico; responda diretamente ao último pedido e, quando houver vários dias, organize a sugestão por Dia 1, Dia 2 etc. "
                        + "Pergunte, quando ainda não souber: "
                        + "origem, destino, datas de ida e volta, duração, orçamento total "
                        + "e limites por categoria, interesses, ritmo, acessibilidade, "
                        + "tipo de hospedagem, quantidade e perfil dos viajantes, "
                        + "e se a pessoa irá de avião, ônibus, carro, moto ou veículo particular. "
                        + "Nunca trate preço estimado como garantia. Diferencie valor observado, "
                        + "histórico, estimado e simulado; indique a fonte e a data de consulta. "
                        + "Quando o contexto financeiro informar orçamento FIXO, nunca recomende uma opção acima do saldo restante. "
                        + "Quando o orçamento for ABERTO, some os valores consultados e informe o acumulado após cada escolha. "
                        + "Quando a pessoa perguntar quanto vai gastar, entregue uma estimativa organizada por passagem, hospedagem, visitas, alimentação, transporte local e extras, usando as escolhas já feitas, quantidade de pessoas e número de dias. Mostre também o saldo ou o que ainda falta definir. "
                        + "Nessa situação, preencha também orcamentoSugerido com valores totais positivos por categoria para a viagem, sem ultrapassar o limite fixo; se não houver dados suficientes, use estimativas conservadoras e identifique-as claramente no texto. "
                        + "Para visitas e alimentação sem preço confirmado, identifique claramente como estimativa ou gratuito; nunca transforme uma estimativa em preço real. "
                        + "Para preços atuais de passagens e hospedagens, use a busca na web quando estiver disponível; informe fonte e data, "
                        + "e nunca apresente um valor web como garantia de compra. "
                        + "Para ônibus, procure tarifas atuais na web quando houver busca disponível; se não encontrar, entregue apenas uma estimativa identificada como simulada. "
                        + "Para carro, use a distância calculada pelo Routes, o modelo e o consumo informado nas preferências e procure o preço atual do combustível no país de origem; "
                        + "calcule litros = distância total / km por litro e custo = litros x preço por litro, sempre mostrando a conta e marcando qualquer dado não encontrado como estimativa. "
                        + "Para cada sugestão de lugar, incentive a conferência no mapa e no Street View. "
                        + "O Locus só realiza reservas e pagamentos simulados, nunca compras reais.\n\n"
                        + request.prompt()
        );
    }

    @PostMapping("/google/places/busca-textual")
    public JsonNode buscarLugares(
            @Valid @RequestBody PlacesSearchRequest request) {

        return placesClient.textSearch(
                request.textQuery(),
                request.languageCode()
        );
    }

    @PostMapping("/google/places/autocomplete")
    public JsonNode autocomplete(
            @Valid @RequestBody AutocompleteRequest request) {

        return placesClient.autocomplete(
                request.input(),
                request.sessionToken()
        );
    }

    @GetMapping("/google/places/{placeId}")
    public JsonNode detalhesLugar(@PathVariable String placeId) {
        return placesClient.details(placeId);
    }

    @GetMapping("/google/places/photo")
    public ResponseEntity<byte[]> fotoLugar(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) Integer maxWidthPx) {

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(placesClient.photo(name, maxWidthPx));
    }

    @PostMapping("/google/routes")
    public JsonNode calcularRota(
            @RequestBody Map<String, Object> request) {

        return routesClient.computeRoute(request);
    }

    @GetMapping("/google/street-view/metadata")
    public JsonNode metadataStreetView(
            @RequestParam @NotBlank String location,
            @RequestParam(required = false) Integer radius) {

        return streetViewClient.metadata(location, radius);
    }

    @GetMapping("/google/street-view/image")
    public ResponseEntity<byte[]> imagemStreetView(
            @RequestParam @NotBlank String location,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height) {

        byte[] imagem = streetViewClient.image(location, width, height);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imagem);
    }

    @GetMapping("/ofertas/voos")
    public JsonNode buscarVoos(
            @RequestParam String originLocationCode,
            @RequestParam String destinationLocationCode,
            @RequestParam String departureDate,
            @RequestParam Integer adults,
            @RequestParam(required = false) String returnDate,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) Integer max) {

        Map<String, String> filters = new LinkedHashMap<>();
        filters.put("originLocationCode", originLocationCode);
        filters.put("destinationLocationCode", destinationLocationCode);
        filters.put("departureDate", departureDate);
        filters.put("adults", String.valueOf(adults));
        filters.put("returnDate", returnDate);
        filters.put("currencyCode", currencyCode);
        filters.put("max", max == null ? null : String.valueOf(max));

        return ofertasSimuladasClient.buscarVoos(filters);
    }

    @GetMapping("/ofertas/localizacoes")
    public JsonNode buscarLocalizacoes(@RequestParam @NotBlank String keyword) {
        return ofertasSimuladasClient.buscarLocalizacoes(keyword);
    }

    @PostMapping("/ofertas/voos/preco")
    public JsonNode precificarVoo(@RequestBody JsonNode flightOffer) {
        return ofertasSimuladasClient.precificarVoo(flightOffer);
    }

    @GetMapping("/ofertas/hoteis")
    public JsonNode listarHoteis(@RequestParam @NotBlank String cityCode) {
        return ofertasSimuladasClient.listarHoteis(cityCode);
    }

    @GetMapping("/ofertas/hoteis/ofertas")
    public JsonNode buscarOfertasDeHotel(
            @RequestParam String hotelIds,
            @RequestParam String adults,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam(required = false) String roomQuantity,
            @RequestParam(required = false) String currency) {

        Map<String, String> filters = new LinkedHashMap<>();
        filters.put("hotelIds", hotelIds);
        filters.put("adults", adults);
        filters.put("checkInDate", checkInDate);
        filters.put("checkOutDate", checkOutDate);
        filters.put("roomQuantity", roomQuantity);
        filters.put("currency", currency);

        return ofertasSimuladasClient.buscarOfertasHotel(filters);
    }

    public record GeminiRequest(
            @NotBlank
            @Size(max = 12000)
            String prompt) {
    }

    public record PlacesSearchRequest(
            @NotBlank
            @Size(max = 300)
            String textQuery,
            String languageCode) {
    }

    public record AutocompleteRequest(
            @NotBlank
            @Size(max = 300)
            String input,
            @Size(max = 200)
            String sessionToken) {
    }
}
