package br.com.locus.integracao.catalogo;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Catálogo curado de pontos turísticos usados como contingência do Places.
 *
 * Os dados abaixo não substituem uma consulta de disponibilidade. Eles existem
 * para que o planejamento continue apresentando lugares específicos, com
 * endereço e coordenadas úteis para os modais do Maps Embed, mesmo quando o
 * Google Places estiver temporariamente limitado.
 */
@Component
public class CatalogoLocaisService {

    private final List<BaseLocal> locais = List.of(
            // Paris, França
            local("catalogo-paris-torre-eiffel", "Torre Eiffel", "Champ de Mars, 5 Avenue Anatole France, 75007 Paris, França", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.7, 0, 48.858370, 2.294481, "Monumento mais conhecido de Paris.", "Paris", "Paris", "torre eiffel", "eiffel tower"),
            local("catalogo-paris-louvre", "Museu do Louvre", "Rue de Rivoli, 75001 Paris, França", "Museu", List.of("museum", "tourist_attraction"), 4.7, 2, 48.860611, 2.337644, "Museu de arte e história no centro de Paris.", "Paris", "Paris", "louvre", "museu do louvre"),
            local("catalogo-paris-arco-triunfo", "Arco do Triunfo", "Place Charles de Gaulle, 75008 Paris, França", "Ponto turístico", List.of("tourist_attraction", "historical_landmark"), 4.7, 1, 48.873792, 2.295028, "Monumento histórico na avenida Champs-Élysées.", "Paris", "Paris", "arco do triunfo", "arc de triomphe"),
            local("catalogo-paris-notre-dame", "Catedral de Notre-Dame de Paris", "6 Parvis Notre-Dame - Place Jean-Paul II, 75004 Paris, França", "Igreja", List.of("church", "tourist_attraction"), 4.7, 0, 48.853034, 2.349923, "Catedral histórica localizada na Île de la Cité.", "Paris", "Paris", "notre dame", "catedral de notre dame"),
            local("catalogo-paris-sacre-coeur", "Basílica de Sacré-Cœur", "35 Rue du Chevalier de la Barre, 75018 Paris, França", "Igreja", List.of("church", "tourist_attraction"), 4.7, 0, 48.886704, 2.343104, "Basílica no alto de Montmartre, com vista para a cidade.", "Paris", "Paris", "sacre coeur", "montmartre"),
            local("catalogo-paris-orsay", "Museu de Orsay", "Esplanade Valéry Giscard d'Estaing, 75007 Paris, França", "Museu", List.of("museum", "art_gallery"), 4.7, 2, 48.860000, 2.326600, "Museu conhecido por sua coleção impressionista.", "Paris", "Paris", "museu de orsay", "orsay"),
            local("catalogo-paris-jardim-luxemburgo", "Jardins de Luxemburgo", "75006 Paris, França", "Parque", List.of("park", "tourist_attraction"), 4.7, 0, 48.846222, 2.337160, "Jardins históricos para caminhada e descanso.", "Paris", "Paris", "jardins de luxemburgo", "luxembourg gardens"),

            // Curaçao
            local("catalogo-curacao-kenepa-grandi", "Playa Kenepa Grandi", "Grote Knip, Sabana Westpunt, Curaçao", "Praia", List.of("beach", "natural_feature"), 4.8, 0, 12.365800, -69.154500, "Praia de areia clara e mar azul no oeste de Curaçao.", "Curaçao", "curacao", "curação", "kenepa grandi", "grote knip", "knip beach"),
            local("catalogo-curacao-kenepa-chiki", "Playa Kenepa Chiki", "Kleine Knip, Sabana Westpunt, Curaçao", "Praia", List.of("beach", "natural_feature"), 4.7, 0, 12.359000, -69.158000, "Praia menor próxima à Playa Kenepa Grandi.", "Curaçao", "curacao", "curação", "kenepa chiki", "kleine knip"),
            local("catalogo-curacao-mambo", "Mambo Beach", "Bapor Kibra, Willemstad, Curaçao", "Praia", List.of("beach", "tourist_attraction"), 4.5, 1, 12.092600, -68.900100, "Faixa de praia com restaurantes, lojas e acesso ao mar.", "Curaçao", "curacao", "curação", "mambo beach", "mambo boulevard"),
            local("catalogo-curacao-ponte-emma", "Ponte Rainha Emma", "Handelskade, Willemstad, Curaçao", "Ponto turístico", List.of("tourist_attraction", "historical_landmark"), 4.6, 0, 12.108300, -68.935000, "Ponte flutuante que liga Punda e Otrobanda.", "Curaçao", "curacao", "curação", "queen emma bridge", "ponte emma"),
            local("catalogo-curacao-aquario", "Curaçao Sea Aquarium", "Bapor Kibra z/n, Willemstad, Curaçao", "Aquário", List.of("aquarium", "tourist_attraction"), 4.4, 2, 12.087400, -68.895500, "Aquário e centro de experiências marinhas.", "Curaçao", "curacao", "curação", "sea aquarium", "aquario de curacao"),
            local("catalogo-curacao-fort-amsterdam", "Fort Amsterdam", "Fort Amsterdam, Willemstad, Curaçao", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.4, 0, 12.107400, -68.934000, "Forte histórico no centro de Willemstad.", "Curaçao", "curacao", "curação", "fort amsterdam"),
            local("catalogo-curacao-christoffel", "Christoffel National Park", "Weg naar Westpunt, Curaçao", "Parque", List.of("park", "natural_feature"), 4.8, 1, 12.333000, -69.123000, "Parque nacional com trilhas e paisagens naturais.", "Curaçao", "curacao", "curação", "christoffel", "christoffel park"),
            local("catalogo-curacao-shete-boka", "Shete Boka National Park", "Weg naar Westpunt, Curaçao", "Parque", List.of("park", "natural_feature"), 4.7, 1, 12.337000, -69.144000, "Parque costeiro com cavernas e ondas do mar do Caribe.", "Curaçao", "curacao", "curação", "shete boka"),

            // Nova York e Estados Unidos
            local("catalogo-ny-times-square", "Times Square", "Manhattan, New York, NY 10036, Estados Unidos", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.7, 0, 40.758000, -73.985500, "Praça iluminada no centro de Manhattan.", "Nova York, Estados Unidos", "nova york", "new york", "nyc", "estados unidos", "usa", "united states", "times square"),
            local("catalogo-ny-central-park", "Central Park", "New York, NY 10022, Estados Unidos", "Parque", List.of("park", "tourist_attraction"), 4.8, 0, 40.782900, -73.965400, "Grande parque urbano no coração de Manhattan.", "Nova York, Estados Unidos", "nova york", "new york", "nyc", "estados unidos", "usa", "central park"),
            local("catalogo-ny-estatua-liberdade", "Estátua da Liberdade", "Liberty Island, New York, NY 10004, Estados Unidos", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.7, 1, 40.689249, -74.044500, "Monumento na Liberty Island, acessado por balsa.", "Nova York, Estados Unidos", "nova york", "new york", "nyc", "estados unidos", "usa", "estatua da liberdade", "statue of liberty"),
            local("catalogo-ny-empire-state", "Empire State Building", "20 W 34th St, New York, NY 10001, Estados Unidos", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.7, 2, 40.748440, -73.985664, "Arranha-céu com observatórios e vista de Manhattan.", "Nova York, Estados Unidos", "nova york", "new york", "nyc", "estados unidos", "usa", "empire state"),
            local("catalogo-ny-ponte-brooklyn", "Ponte do Brooklyn", "Brooklyn Bridge, New York, NY 10038, Estados Unidos", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.8, 0, 40.706086, -73.996864, "Ponte histórica sobre o East River.", "Nova York, Estados Unidos", "nova york", "new york", "nyc", "estados unidos", "usa", "brooklyn bridge", "ponte do brooklyn"),
            local("catalogo-ny-911", "Memorial e Museu do 11 de Setembro", "180 Greenwich St, New York, NY 10007, Estados Unidos", "Museu", List.of("museum", "historical_landmark"), 4.7, 2, 40.711507, -74.013376, "Memorial e museu no complexo do World Trade Center.", "Nova York, Estados Unidos", "nova york", "new york", "nyc", "estados unidos", "usa", "9/11 memorial", "memorial 11 de setembro"),
            local("catalogo-ny-rockefeller", "Top of the Rock", "30 Rockefeller Plaza, New York, NY 10112, Estados Unidos", "Ponto turístico", List.of("tourist_attraction", "observation_deck"), 4.7, 2, 40.759011, -73.980016, "Observatório no Rockefeller Center.", "Nova York, Estados Unidos", "nova york", "new york", "nyc", "estados unidos", "usa", "top of the rock", "rockefeller center"),
            local("catalogo-miami-south-beach", "South Beach", "Ocean Drive, Miami Beach, FL 33139, Estados Unidos", "Praia", List.of("beach", "tourist_attraction"), 4.6, 0, 25.782600, -80.130000, "Praia e calçadão conhecidos de Miami Beach.", "Miami, Estados Unidos", "miami", "estados unidos", "usa", "south beach"),
            local("catalogo-orlando-universal", "Universal Studios Florida", "6000 Universal Blvd, Orlando, FL 32819, Estados Unidos", "Parque", List.of("theme_park", "tourist_attraction"), 4.7, 2, 28.474600, -81.466500, "Parque temático e complexo de entretenimento em Orlando.", "Orlando, Estados Unidos", "orlando", "estados unidos", "usa", "universal studios"),
            local("catalogo-washington-lincoln", "Lincoln Memorial", "2 Lincoln Memorial Cir NW, Washington, DC 20002, Estados Unidos", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.9, 0, 38.889300, -77.050200, "Memorial nacional no National Mall.", "Washington, Estados Unidos", "washington", "estados unidos", "usa", "lincoln memorial"),
            local("catalogo-los-angeles-hollywood", "Calçada da Fama de Hollywood", "6801 Hollywood Blvd, Los Angeles, CA 90028, Estados Unidos", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.1, 0, 34.101600, -118.326700, "Calçada com estrelas de artistas em Hollywood.", "Los Angeles, Estados Unidos", "los angeles", "hollywood", "estados unidos", "usa", "hollywood walk of fame"),

            // Portugal
            local("catalogo-lisboa-belem", "Torre de Belém", "Av. Brasília, 1400-038 Lisboa, Portugal", "Ponto turístico", List.of("tourist_attraction", "historical_landmark"), 4.6, 1, 38.691600, -9.216000, "Torre histórica à margem do rio Tejo.", "Lisboa, Portugal", "lisboa", "portugal", "belem", "torre de belem"),
            local("catalogo-lisboa-jeronimos", "Mosteiro dos Jerónimos", "Praça do Império, 1400-206 Lisboa, Portugal", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.6, 1, 38.697900, -9.206400, "Mosteiro histórico no bairro de Belém.", "Lisboa, Portugal", "lisboa", "portugal", "jeronimos", "mosteiro dos jeronimos"),
            local("catalogo-lisboa-comercio", "Praça do Comércio", "Praça do Comércio, 1100-148 Lisboa, Portugal", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.7, 0, 38.707750, -9.136592, "Praça histórica aberta para o rio Tejo.", "Lisboa, Portugal", "lisboa", "portugal", "praca do comercio", "terreiro do paco"),
            local("catalogo-lisboa-castelo", "Castelo de São Jorge", "Rua de Santa Cruz do Castelo, 1100-129 Lisboa, Portugal", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.6, 1, 38.713900, -9.133500, "Castelo histórico no alto da Alfama.", "Lisboa, Portugal", "lisboa", "portugal", "castelo de sao jorge", "alfama"),
            local("catalogo-porto-livraria", "Livraria Lello", "Rua das Carmelitas 144, 4050-161 Porto, Portugal", "Ponto turístico", List.of("tourist_attraction", "book_store"), 4.4, 1, 41.146800, -8.614900, "Livraria histórica no centro do Porto.", "Porto, Portugal", "porto", "portugal", "livraria lello"),
            local("catalogo-porto-ribeira", "Ribeira do Porto", "Praça da Ribeira, 4050-513 Porto, Portugal", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.7, 0, 41.140700, -8.613100, "Margem histórica do Douro no centro do Porto.", "Porto, Portugal", "porto", "portugal", "ribeira", "porto"),
            local("catalogo-porto-ponte-luis", "Ponte Dom Luís I", "Ponte Luís I, Porto, Portugal", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.8, 0, 41.140100, -8.609800, "Ponte metálica sobre o rio Douro.", "Porto, Portugal", "porto", "portugal", "ponte dom luis", "ponte d luís", "porto"),
            local("catalogo-madeira-cabo-girao", "Cabo Girão", "Cabo Girão, Câmara de Lobos, Madeira, Portugal", "Atração natural", List.of("natural_feature", "tourist_attraction"), 4.5, 1, 32.656400, -17.005900, "Mirante sobre uma das falésias mais altas da Europa.", "Madeira, Portugal", "madeira", "funchal", "portugal", "cabo girao"),
            local("catalogo-madeira-pico-arieiro", "Pico do Arieiro", "Pico do Arieiro, 9030-515 Madeira, Portugal", "Atração natural", List.of("natural_feature", "tourist_attraction"), 4.9, 0, 32.735000, -16.928700, "Mirante de montanha com trilhas e vistas da Madeira.", "Madeira, Portugal", "madeira", "funchal", "portugal", "pico do arieiro")
    );

    public List<CatalogoLocal> buscar(String consulta) {
        String busca = normalizar(consulta);
        if (busca.isBlank()) {
            return List.of();
        }
        String agora = Instant.now().toString();
        return locais.stream()
                .map(local -> new Resultado(local, pontuacao(local, busca)))
                .filter(resultado -> resultado.pontos > 0)
                .sorted(Comparator.comparingInt(Resultado::pontos).reversed())
                .limit(12)
                .map(resultado -> new CatalogoLocal(
                        resultado.local.placeId,
                        resultado.local.nome,
                        resultado.local.endereco,
                        resultado.local.categoria,
                        resultado.local.tipos,
                        resultado.local.avaliacao,
                        resultado.local.nivelPreco,
                        resultado.local.lat,
                        resultado.local.lng,
                        resultado.local.descricao,
                        "Catálogo Locus",
                        agora,
                        false
                ))
                .toList();
    }

    private int pontuacao(BaseLocal local, String busca) {
        int pontos = 0;
        for (String alias : local.aliases) {
            String termo = normalizar(alias);
            if (!termo.isBlank() && busca.contains(termo)) {
                pontos += 25 + termo.length();
            }
        }
        String categoria = normalizar(local.categoria);
        if (busca.contains("praia") && categoria.contains("praia")) pontos += 8;
        if (busca.contains("museu") && categoria.contains("museu")) pontos += 8;
        if ((busca.contains("parque") || busca.contains("natureza")) && (categoria.contains("parque") || categoria.contains("natural"))) pontos += 8;
        if (busca.contains("ponto turist") && categoria.contains("ponto turist")) pontos += 5;
        return pontos;
    }

    private static String normalizar(String valor) {
        return Normalizer.normalize(valor == null ? "" : valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private static BaseLocal local(
            String placeId,
            String nome,
            String endereco,
            String categoria,
            List<String> tipos,
            double avaliacao,
            int nivelPreco,
            double lat,
            double lng,
            String descricao,
            String destino,
            String... aliases) {
        List<String> termos = new ArrayList<>();
        termos.add(destino);
        termos.addAll(Arrays.asList(aliases));
        return new BaseLocal(placeId, nome, endereco, categoria, tipos, avaliacao, nivelPreco, lat, lng, descricao, destino, termos);
    }

    private record Resultado(BaseLocal local, int pontos) {
    }

    private record BaseLocal(
            String placeId,
            String nome,
            String endereco,
            String categoria,
            List<String> tipos,
            double avaliacao,
            int nivelPreco,
            double lat,
            double lng,
            String descricao,
            String destino,
            List<String> aliases) {
    }

    public record CatalogoLocal(
            String placeId,
            String nome,
            String endereco,
            String categoria,
            List<String> tipos,
            double avaliacao,
            int nivelPreco,
            double lat,
            double lng,
            String descricao,
            String fonte,
            String consultadoEm,
            boolean demonstrativo) {
    }
}
