package br.com.locus.integracao;

import br.com.locus.integracao.amadeus.AmadeusClient;
import br.com.locus.integracao.google.GooglePlacesClient;
import br.com.locus.integracao.google.GoogleRoutesClient;
import br.com.locus.integracao.google.GoogleStreetViewClient;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/integracoes")
public class IntegracoesController {

    private final AssistenteViagemClient assistenteViagemClient;
    private final GooglePlacesClient placesClient;
    private final GoogleRoutesClient routesClient;
    private final GoogleStreetViewClient streetViewClient;
    private final AmadeusClient amadeusClient;

    public IntegracoesController(
            AssistenteViagemClient assistenteViagemClient,
            GooglePlacesClient placesClient,
            GoogleRoutesClient routesClient,
            GoogleStreetViewClient streetViewClient,
            AmadeusClient amadeusClient) {

        this.assistenteViagemClient = assistenteViagemClient;
        this.placesClient = placesClient;
        this.routesClient = routesClient;
        this.streetViewClient = streetViewClient;
        this.amadeusClient = amadeusClient;
    }

    @PostMapping("/gemini/conteudo")
    public JsonNode gerarConteudo(
            @Valid @RequestBody GeminiRequest request) {

        return assistenteViagemClient.gerar(
                "Você é o assistente de planejamento do Locus. "
                        + "Conduza a conversa por etapas e pergunte, quando ainda não souber: "
                        + "origem, destino, datas de ida e volta, duração, orçamento total "
                        + "e limites por categoria, interesses, ritmo, acessibilidade, "
                        + "tipo de hospedagem, quantidade e perfil dos viajantes, "
                        + "e se a pessoa irá de avião, ônibus, carro, moto ou veículo particular. "
                        + "Nunca trate preço estimado como garantia. Diferencie valor observado, "
                        + "histórico, estimado e simulado; indique a fonte e a data de consulta. "
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

    @GetMapping({"/ofertas/voos", "/amadeus/voos"})
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

        return amadeusClient.searchFlightOffers(filters);
    }

    @PostMapping({"/ofertas/voos/preco", "/amadeus/voos/preco"})
    public JsonNode precificarVoo(@RequestBody JsonNode flightOffer) {
        return amadeusClient.priceFlightOffer(flightOffer);
    }

    @GetMapping({"/ofertas/hoteis", "/amadeus/hoteis"})
    public JsonNode listarHoteis(@RequestParam @NotBlank String cityCode) {
        return amadeusClient.listHotels(cityCode);
    }

    @GetMapping({"/ofertas/hoteis/ofertas", "/amadeus/hoteis/ofertas"})
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

        return amadeusClient.searchHotels(filters);
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
