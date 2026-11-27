package br.com.locus.integracao.ofertas;

import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

/**
 * Catálogo demonstrativo do Locus para voos e hospedagens.
 *
 * Ele é deliberadamente local: não chama provedores comerciais nem finge disponibilidade
 * de compra. A interface identifica cada valor como simulado para a pessoa
 * poder montar e testar o roteiro sem uma integração comercial.
 */
@Component
public class OfertasSimuladasClient {

    private final ObjectMapper mapper;

    public OfertasSimuladasClient(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonNode buscarVoos(Map<String, String> filtros) {
        String origem = valor(filtros, "originLocationCode", "GRU");
        String destino = valor(filtros, "destinationLocationCode", "LIS");
        LocalDate ida = data(filtros.get("departureDate"), LocalDate.now().plusDays(30));
        String volta = filtros.get("returnDate");
        String moeda = valor(filtros, "currencyCode", "BRL");
        int limite = inteiro(filtros.get("max"), 3);
        int adultos = inteiro(filtros.get("adults"), 1);
        boolean idaEVolta = volta != null && !volta.isBlank();

        ObjectNode resposta = mapper.createObjectNode();
        resposta.putObject("meta").put("count", Math.min(limite, 3));
        ArrayNode ofertas = resposta.putArray("data");
        for (int i = 0; i < Math.min(limite, 3); i++) {
            ofertas.add(ofertaVoo(origem, destino, ida, volta, moeda, adultos, i));
        }
        return resposta;
    }

    public JsonNode buscarLocalizacoes(String palavra) {
        String consulta = palavra == null ? "" : palavra.toLowerCase(Locale.ROOT);
        ObjectNode resposta = mapper.createObjectNode();
        ArrayNode dados = resposta.putArray("data");
        if (consulta.contains("madeira") || consulta.contains("funchal")) {
            dados.add(localizacao("FNC", "Funchal", "Portugal"));
        } else if (consulta.contains("salvador")) {
            dados.add(localizacao("SSA", "Salvador", "Brasil"));
        } else if (consulta.contains("lisboa") || consulta.contains("lisbon") || consulta.contains("portugal")) {
            dados.add(localizacao("LIS", "Lisboa", "Portugal"));
        } else if (consulta.contains("curacao") || consulta.contains("curaçao") || consulta.contains("willemstad")) {
            dados.add(localizacao("CUR", "Willemstad", "Curaçao"));
        }
        return resposta;
    }

    public JsonNode precificarVoo(JsonNode oferta) {
        ObjectNode resposta = mapper.createObjectNode();
        ObjectNode dados = resposta.putObject("data");
        dados.put("type", "flight-offers-pricing");
        dados.putArray("flightOffers").add(oferta.deepCopy());
        dados.put("source", "LOCUS");
        dados.put("simulated", true);
        return resposta;
    }

    public JsonNode listarHoteis(String cidade) {
        String codigo = cidade == null || cidade.isBlank() ? "LIS" : cidade.trim().toUpperCase(Locale.ROOT);
        double[] centro = centro(codigo);
        ObjectNode resposta = mapper.createObjectNode();
        resposta.putObject("meta").put("count", 3);
        ArrayNode dados = resposta.putArray("data");
        dados.add(resumoHotel("LOC-" + codigo + "-001", "Locus Central Hotel", codigo, centro[0], centro[1], 0));
        dados.add(resumoHotel("LOC-" + codigo + "-002", "Rota Verde Suites", codigo, centro[0] + 0.004, centro[1] - 0.004, 1));
        dados.add(resumoHotel("LOC-" + codigo + "-003", "Vista do Viajante", codigo, centro[0] - 0.005, centro[1] + 0.006, 2));
        return resposta;
    }

    public JsonNode buscarOfertasHotel(Map<String, String> filtros) {
        String ids = valor(filtros, "hotelIds", "LOC-LIS-001");
        LocalDate entrada = data(filtros.get("checkInDate"), LocalDate.now().plusDays(30));
        LocalDate saida = data(filtros.get("checkOutDate"), entrada.plusDays(3));
        String moeda = valor(filtros, "currency", "BRL");
        int adultos = inteiro(filtros.get("adults"), 1);
        int quartos = inteiro(filtros.get("roomQuantity"), 1);
        long noites = Math.max(1, ChronoUnit.DAYS.between(entrada, saida));
        ObjectNode resposta = mapper.createObjectNode();
        ArrayNode dados = resposta.putArray("data");
        int indice = 0;
        for (String id : ids.split(",")) {
            if (!id.isBlank()) dados.add(ofertaHotel(id.trim(), entrada, saida, moeda, adultos, quartos, noites, indice++));
        }
        resposta.putObject("meta").put("count", dados.size());
        return resposta;
    }

    private ObjectNode localizacao(String codigo, String cidade, String pais) {
        ObjectNode local = mapper.createObjectNode();
        local.put("type", "location").put("subType", "CITY").put("iataCode", codigo);
        local.putObject("name").put("cityName", cidade);
        local.putObject("address").put("cityName", cidade).put("countryName", pais);
        return local;
    }

    private ObjectNode ofertaVoo(String origem, String destino, LocalDate ida, String volta, String moeda, int adultos, int indice) {
        String companhia = indice == 0 ? "LA" : indice == 1 ? "AD" : "G3";
        String numero = String.valueOf(3000 + indice * 117);
        double faixaBase = faixaBaseVoo(origem, destino);
        boolean idaEVolta = volta != null && !volta.isBlank();
        double multiplicadorIdaEVolta = idaEVolta ? 1.78d : 1d;
        BigDecimal total = BigDecimal.valueOf((faixaBase + indice * 180d) * multiplicadorIdaEVolta * adultos)
                .setScale(2, RoundingMode.HALF_UP);
        LocalDateTime partida = LocalDateTime.of(ida, LocalTime.of(7 + indice * 4, 20));
        ObjectNode oferta = mapper.createObjectNode();
        oferta.put("type", "flight-offer").put("id", "LOCUS-FLIGHT-" + (indice + 1));
        oferta.put("source", "LOCUS").put("simulated", true).put("oneWay", !idaEVolta)
                .put("travelerCount", adultos).put("roundTrip", idaEVolta);
        ArrayNode itinerarios = oferta.putArray("itineraries");
        itinerarios.add(itinerario(origem, destino, partida, companhia, numero, indice));
        if (volta != null && !volta.isBlank()) {
            itinerarios.add(itinerario(destino, origem, LocalDateTime.of(data(volta, ida.plusDays(7)), LocalTime.of(16, 10 + indice * 10)), companhia, String.valueOf(Integer.parseInt(numero) + 1), indice));
        }
        ObjectNode preco = oferta.putObject("price");
        preco.put("currency", moeda.toUpperCase(Locale.ROOT)).put("total", total).put("base", total.multiply(BigDecimal.valueOf(.88)).setScale(2, RoundingMode.HALF_UP));
        preco.putArray("fees").addObject().put("amount", total.multiply(BigDecimal.valueOf(.12)).setScale(2, RoundingMode.HALF_UP));
        oferta.putArray("validatingAirlineCodes").add(companhia);
        return oferta;
    }

    private ObjectNode itinerario(String origem, String destino, LocalDateTime partida, String companhia, String numero, int indice) {
        ObjectNode itinerario = mapper.createObjectNode();
        itinerario.put("duration", indice == 0 ? "PT2H35M" : indice == 1 ? "PT3H10M" : "PT2H50M");
        ObjectNode segmento = itinerario.putArray("segments").addObject();
        segmento.putObject("departure").put("iataCode", origem).put("at", partida.atOffset(ZoneOffset.UTC).toString());
        segmento.putObject("arrival").put("iataCode", destino).put("at", partida.plusHours(2).plusMinutes(35).atOffset(ZoneOffset.UTC).toString());
        segmento.put("carrierCode", companhia).put("number", numero);
        return itinerario;
    }

    private ObjectNode resumoHotel(String id, String nome, String cidade, double latitude, double longitude, int indice) {
        ObjectNode hotel = mapper.createObjectNode();
        hotel.put("type", "hotel").put("hotelId", id).put("name", nome).put("cityCode", cidade);
        hotel.put("source", "LOCUS").put("simulated", true).put("imageUrl", imagemHotel(indice));
        hotel.putObject("geoCode").put("latitude", latitude).put("longitude", longitude);
        return hotel;
    }

    private ObjectNode ofertaHotel(String id, LocalDate entrada, LocalDate saida, String moeda, int adultos, int quartos, long noites, int indice) {
        String codigo = id.length() >= 8 ? id.substring(4, 7) : "LIS";
        double[] centro = centro(codigo);
        ObjectNode item = mapper.createObjectNode();
        item.put("type", "hotel-offers").put("source", "LOCUS").put("simulated", true);
        item.set("hotel", resumoHotel(id, nomeHotel(indice), codigo, centro[0] + indice * .004, centro[1] - indice * .004, indice));
        ObjectNode oferta = item.putArray("offers").addObject();
        oferta.put("id", "LOCUS-HOTEL-OFFER-" + (indice + 1)).put("checkInDate", entrada.toString()).put("checkOutDate", saida.toString())
                .put("roomQuantity", quartos).put("adults", adultos).put("nights", noites);
        oferta.putObject("room").put("type", "STANDARD").put("typeEstimated", "DOUBLE");
        BigDecimal diaria = diariaHotel(codigo, indice);
        BigDecimal total = diaria.multiply(BigDecimal.valueOf(noites)).multiply(BigDecimal.valueOf(quartos)).setScale(2, RoundingMode.HALF_UP);
        oferta.putObject("price").put("currency", moeda.toUpperCase(Locale.ROOT)).put("total", total)
                .put("nightly", diaria).put("nights", noites);
        return item;
    }

    private BigDecimal diariaHotel(String codigo, int indice) {
        long[] faixas = switch (codigo.toUpperCase(Locale.ROOT)) {
            case "PAR", "LON", "ZRH", "NYC" -> new long[]{820, 1150, 1550};
            case "LIS", "FNC", "OPO" -> new long[]{520, 720, 980};
            case "CUR", "AUA" -> new long[]{680, 900, 1250};
            case "SSA", "REC", "FOR", "NAT", "GIG", "GRU" -> new long[]{360, 520, 760};
            default -> new long[]{420, 620, 900};
        };
        return BigDecimal.valueOf(faixas[Math.max(0, Math.min(indice, faixas.length - 1))]);
    }

    private double faixaBaseVoo(String origem, String destino) {
        if (origem.equalsIgnoreCase(destino)) return 420d;
        boolean brasil = Setores.BRASIL.contains(origem.toUpperCase(Locale.ROOT));
        boolean europa = Setores.EUROPA.contains(destino.toUpperCase(Locale.ROOT));
        boolean caribe = Setores.CARIBE.contains(destino.toUpperCase(Locale.ROOT));
        if (brasil && europa) return 2600d;
        if (brasil && caribe) return 1900d;
        if (brasil) return 950d;
        return 1400d;
    }

    private static final class Setores {
        private static final java.util.Set<String> BRASIL = java.util.Set.of("GRU", "GIG", "SSA", "REC", "FOR", "NAT");
        private static final java.util.Set<String> EUROPA = java.util.Set.of("LIS", "FNC", "MAD", "PAR", "LON", "ROM");
        private static final java.util.Set<String> CARIBE = java.util.Set.of("CUR", "SXM", "AUA", "SDQ", "HAV");
    }

    private String nomeHotel(int indice) {
        return indice == 0 ? "Locus Central Hotel" : indice == 1 ? "Rota Verde Suites" : "Vista do Viajante";
    }

    private String imagemHotel(int indice) {
        return new String[]{
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=900&q=80"
        }[Math.max(0, Math.min(indice, 2))];
    }

    private double[] centro(String codigo) {
        return switch (codigo) {
            case "CUR" -> new double[]{12.1090, -68.9330};
            case "FNC" -> new double[]{32.6500, -16.9100};
            case "SSA" -> new double[]{-12.9714, -38.5014};
            case "GRU" -> new double[]{-23.4356, -46.4731};
            case "GIG" -> new double[]{-22.9068, -43.1729};
            case "REC" -> new double[]{-8.0476, -34.8770};
            case "FOR" -> new double[]{-3.7319, -38.5267};
            default -> new double[]{38.7223, -9.1393};
        };
    }

    private String valor(Map<String, String> mapa, String chave, String padrao) {
        String valor = mapa.get(chave);
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    private int inteiro(String valor, int padrao) {
        try { return Math.max(1, Integer.parseInt(valor)); } catch (RuntimeException ignored) { return padrao; }
    }

    private LocalDate data(String valor, LocalDate padrao) {
        try { return valor == null || valor.isBlank() ? padrao : LocalDate.parse(valor); } catch (RuntimeException ignored) { return padrao; }
    }
}
