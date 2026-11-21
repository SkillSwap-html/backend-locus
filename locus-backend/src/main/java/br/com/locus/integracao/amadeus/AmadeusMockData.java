package br.com.locus.integracao.amadeus;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;

/**
 * Catálogo local usado quando o projeto está sem credenciais do Amadeus.
 * A estrutura imita os campos principais das respostas Self-Service para que
 * o frontend não precise conhecer a origem dos dados.
 */
@Component
public class AmadeusMockData {

    private final ObjectMapper mapper;

    public AmadeusMockData(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonNode searchFlights(Map<String, String> filters) {
        String origin = value(filters, "originLocationCode", "GRU");
        String destination = value(filters, "destinationLocationCode", "LIS");
        String departure = value(filters, "departureDate", LocalDate.now().plusDays(30).toString());
        String returnDate = filters.get("returnDate");
        String currency = value(filters, "currencyCode", "BRL");
        int max = parsePositiveInt(filters.get("max"), 3);

        ObjectNode response = mapper.createObjectNode();
        response.putObject("meta").put("count", Math.min(max, 3));
        ArrayNode data = response.putArray("data");

        for (int index = 0; index < Math.min(max, 3); index++) {
            data.add(flightOffer(origin, destination, departure, returnDate, currency, index));
        }
        return response;
    }

    public JsonNode searchLocations(String keyword) {
        String query = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        ObjectNode response = mapper.createObjectNode();
        ArrayNode data = response.putArray("data");
        if (query.contains("madeira") || query.contains("funchal")) {
            data.add(location("FNC", "Funchal", "Portugal"));
        } else if (query.contains("salvador")) {
            data.add(location("SSA", "Salvador", "Brasil"));
        } else if (query.contains("lisboa") || query.contains("lisbon") || query.contains("portugal")) {
            data.add(location("LIS", "Lisboa", "Portugal"));
        } else if (query.contains("curacao") || query.contains("curaçao") || query.contains("willemstad")) {
            data.add(location("CUR", "Willemstad", "Curaçao"));
        }
        return response;
    }

    private ObjectNode location(String code, String city, String country) {
        ObjectNode location = mapper.createObjectNode();
        location.put("type", "location");
        location.put("subType", "CITY");
        location.put("iataCode", code);
        location.putObject("name").put("cityName", city);
        location.putObject("address").put("cityName", city).put("countryName", country);
        return location;
    }

    public JsonNode priceFlight(JsonNode flightOffer) {
        ObjectNode response = mapper.createObjectNode();
        ObjectNode data = response.putObject("data");
        data.put("type", "flight-offers-pricing");
        ArrayNode offers = data.putArray("flightOffers");
        offers.add(flightOffer.deepCopy());
        data.put("source", "LOCUS");
        data.put("simulated", true);
        return response;
    }

    public JsonNode listHotels(String cityCode) {
        String code = cityCode == null || cityCode.isBlank()
                ? "LIS"
                : cityCode.trim().toUpperCase(Locale.ROOT);

        ObjectNode response = mapper.createObjectNode();
        response.putObject("meta").put("count", 3);
        ArrayNode data = response.putArray("data");
        double[] centro = centroDaCidade(code);
        data.add(hotelSummary("LOC-" + code + "-001", "Locus Central Hotel", code, centro[0], centro[1]));
        data.add(hotelSummary("LOC-" + code + "-002", "Rota Verde Suites", code, centro[0] + 0.004, centro[1] - 0.004));
        data.add(hotelSummary("LOC-" + code + "-003", "Vista do Viajante", code, centro[0] - 0.005, centro[1] + 0.006));
        return response;
    }

    private double[] centroDaCidade(String cityCode) {
        return switch (cityCode) {
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

    public JsonNode searchHotels(Map<String, String> filters) {
        String hotelIds = value(filters, "hotelIds", "LOC-LIS-001");
        String checkIn = value(filters, "checkInDate", LocalDate.now().plusDays(30).toString());
        String checkOut = value(filters, "checkOutDate", LocalDate.parse(checkIn).plusDays(3).toString());
        String currency = value(filters, "currency", "BRL");
        String[] ids = hotelIds.split(",");

        ObjectNode response = mapper.createObjectNode();
        response.putObject("meta").put("count", ids.length);
        ArrayNode data = response.putArray("data");
        for (int index = 0; index < ids.length; index++) {
            String id = ids[index].trim();
            if (!id.isBlank()) {
                data.add(hotelOffer(id, checkIn, checkOut, currency, index));
            }
        }
        return response;
    }

    private ObjectNode flightOffer(
            String origin,
            String destination,
            String departure,
            String returnDate,
            String currency,
            int index) {

        String carrier = index == 0 ? "LA" : index == 1 ? "AD" : "G3";
        String flightNumber = String.valueOf(3000 + index * 117);
        BigDecimal total = BigDecimal.valueOf(1180 + index * 245)
                .setScale(2, RoundingMode.HALF_UP);
        LocalDate departureDate = LocalDate.parse(departure);
        LocalDateTime departureAt = LocalDateTime.of(
                departureDate,
                LocalTime.of(7 + index * 4, 20)
        );

        ObjectNode offer = mapper.createObjectNode();
        offer.put("type", "flight-offer");
        offer.put("id", "MOCK-FLIGHT-" + (index + 1));
        offer.put("source", "LOCUS");
        offer.put("simulated", true);
        offer.put("instantTicketingRequired", false);
        offer.put("oneWay", returnDate == null || returnDate.isBlank());
        offer.put("lastTicketingDate", departureDate.minusDays(2).toString());
        offer.put("numberOfBookableSeats", 6 - index);
        ArrayNode itineraries = offer.putArray("itineraries");
        itineraries.add(itinerary(origin, destination, departureAt, carrier, flightNumber, index));

        if (returnDate != null && !returnDate.isBlank()) {
            itineraries.add(itinerary(
                    destination,
                    origin,
                    LocalDateTime.of(LocalDate.parse(returnDate), LocalTime.of(16, 10 + index * 10)),
                    carrier,
                    String.valueOf(Integer.parseInt(flightNumber) + 1),
                    index
            ));
        }

        ObjectNode price = offer.putObject("price");
        price.put("currency", currency.toUpperCase(Locale.ROOT));
        price.put("total", total);
        price.put("base", total.multiply(BigDecimal.valueOf(0.88)).setScale(2, RoundingMode.HALF_UP));
        price.putArray("fees").addObject().put("amount", total.multiply(BigDecimal.valueOf(0.12)).setScale(2, RoundingMode.HALF_UP));
        price.put("grandTotal", total);
        offer.putObject("pricingOptions").putArray("fareType").add("PUBLISHED");
        offer.putArray("validatingAirlineCodes").add(carrier);
        return offer;
    }

    private ObjectNode itinerary(
            String origin,
            String destination,
            LocalDateTime departure,
            String carrier,
            String flightNumber,
            int index) {

        ObjectNode itinerary = mapper.createObjectNode();
        itinerary.put("duration", index == 0 ? "PT2H35M" : index == 1 ? "PT3H10M" : "PT2H50M");
        ArrayNode segments = itinerary.putArray("segments");
        ObjectNode segment = segments.addObject();
        segment.put("id", "MOCK-SEGMENT-" + flightNumber);
        segment.putObject("departure")
                .put("iataCode", origin)
                .put("at", departure.atOffset(ZoneOffset.UTC).toString());
        segment.putObject("arrival")
                .put("iataCode", destination)
                .put("at", departure.plusHours(2).plusMinutes(35).atOffset(ZoneOffset.UTC).toString());
        segment.put("carrierCode", carrier);
        segment.put("number", flightNumber);
        segment.putObject("aircraft").put("code", index == 1 ? "A320" : "E195");
        return itinerary;
    }

    private ObjectNode hotelSummary(
            String hotelId,
            String name,
            String cityCode,
            double latitude,
            double longitude) {

        ObjectNode hotel = mapper.createObjectNode();
        hotel.put("type", "hotel");
        hotel.put("hotelId", hotelId);
        hotel.put("name", name);
        hotel.put("cityCode", cityCode);
        hotel.put("source", "LOCUS");
        hotel.put("simulated", true);
        hotel.put("imageUrl", imagemHotel(cityCode, hotelId));
        hotel.putObject("geoCode").put("latitude", latitude).put("longitude", longitude);
        hotel.putObject("address").put("countryCode", "BR");
        return hotel;
    }

    private ObjectNode hotelOffer(
            String hotelId,
            String checkIn,
            String checkOut,
            String currency,
            int index) {

        ObjectNode item = mapper.createObjectNode();
        item.put("type", "hotel-offers");
        item.put("source", "LOCUS");
        item.put("simulated", true);
        double[] centro = centroDaCidade(hotelId.length() >= 8 ? hotelId.substring(4, 7) : "LIS");
        item.set("hotel", hotelSummary(
                hotelId,
                index == 0 ? "Locus Central Hotel" : index == 1 ? "Rota Verde Suites" : "Vista do Viajante",
                hotelId.length() >= 8 ? hotelId.substring(4, 7) : "LIS",
                centro[0] + index * 0.004,
                centro[1] - index * 0.004
        ));

        ArrayNode offers = item.putArray("offers");
        ObjectNode offer = offers.addObject();
        offer.put("id", "MOCK-HOTEL-OFFER-" + (index + 1));
        offer.put("checkInDate", checkIn);
        offer.put("checkOutDate", checkOut);
        offer.put("roomQuantity", 1);
        offer.putObject("room").put("type", "STANDARD").put("typeEstimated", "DOUBLE");
        offer.putObject("price")
                .put("currency", currency.toUpperCase(Locale.ROOT))
                .put("total", BigDecimal.valueOf(420 + index * 145).setScale(2, RoundingMode.HALF_UP));
        offer.putObject("policies").put("paymentType", "GUARANTEE");
        return item;
    }

    private String imagemHotel(String cityCode, String hotelId) {
        int indice = hotelId.endsWith("002") ? 1 : hotelId.endsWith("003") ? 2 : 0;
        String[] imagens = {
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=900&q=80"
        };
        return imagens[indice] + "&locus_city=" + cityCode;
    }

    private static String value(Map<String, String> values, String key, String fallback) {
        String value = values.get(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static int parsePositiveInt(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : fallback;
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }
}
