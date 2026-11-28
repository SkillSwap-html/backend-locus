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
            local("catalogo-madeira-pico-arieiro", "Pico do Arieiro", "Pico do Arieiro, 9030-515 Madeira, Portugal", "Atração natural", List.of("natural_feature", "tourist_attraction"), 4.9, 0, 32.735000, -16.928700, "Mirante de montanha com trilhas e vistas da Madeira.", "Madeira, Portugal", "madeira", "funchal", "portugal", "pico do arieiro"),

            // Reino Unido — Londres
            local("catalogo-londres-big-ben", "Big Ben", "Palace of Westminster, London SW1A 0AA, Reino Unido", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.6, 0, 51.500729, -0.124625, "Relógio do Palácio de Westminster, às margens do Tâmisa.", "Londres, Reino Unido", "londres", "london", "reino unido", "uk", "big ben"),
            local("catalogo-londres-london-eye", "London Eye", "Lambeth, London SE1 7PB, Reino Unido", "Ponto turístico", List.of("tourist_attraction", "observation_deck"), 4.5, 2, 51.503324, -0.119543, "Roda-gigante com vista panorâmica de Londres.", "Londres, Reino Unido", "londres", "london", "reino unido", "uk", "london eye"),
            local("catalogo-londres-tower", "Torre de Londres", "St Katharine's & Wapping, London EC3N 4AB, Reino Unido", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.6, 2, 51.508112, -0.075949, "Fortaleza histórica junto ao rio Tâmisa.", "Londres, Reino Unido", "londres", "london", "reino unido", "uk", "tower of london", "torre de londres"),
            local("catalogo-londres-buckingham", "Palácio de Buckingham", "London SW1A 1AA, Reino Unido", "Ponto turístico", List.of("tourist_attraction", "historical_landmark"), 4.5, 0, 51.501364, -0.141890, "Residência real e local da troca da guarda.", "Londres, Reino Unido", "londres", "london", "reino unido", "uk", "buckingham palace"),

            // Itália — Roma
            local("catalogo-roma-coliseu", "Coliseu", "Piazza del Colosseo 1, 00184 Roma, Itália", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.7, 2, 41.890210, 12.492231, "Anfiteatro romano e um dos principais símbolos de Roma.", "Roma, Itália", "roma", "rome", "italia", "italy", "coliseu", "colosseum"),
            local("catalogo-roma-trevi", "Fonte de Trevi", "Piazza di Trevi, 00187 Roma, Itália", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.8, 0, 41.900932, 12.483313, "Fonte barroca no centro histórico de Roma.", "Roma, Itália", "roma", "rome", "italia", "italy", "fonte de trevi", "trevi fountain"),
            local("catalogo-roma-panteao", "Panteão de Roma", "Piazza della Rotonda, 00186 Roma, Itália", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.8, 1, 41.898610, 12.476872, "Edifício romano preservado no centro histórico.", "Roma, Itália", "roma", "rome", "italia", "italy", "panteao", "pantheon"),
            local("catalogo-roma-vaticano", "Museus do Vaticano", "Viale Vaticano, 00165 Roma, Itália", "Museu", List.of("museum", "tourist_attraction"), 4.7, 2, 41.906487, 12.453641, "Complexo de museus e galerias próximo à Cidade do Vaticano.", "Roma, Itália", "roma", "rome", "italia", "italy", "museus do vaticano", "vatican museums"),

            // Espanha — Barcelona
            local("catalogo-barcelona-sagrada-familia", "Basílica da Sagrada Família", "Carrer de Mallorca 401, 08013 Barcelona, Espanha", "Igreja", List.of("church", "tourist_attraction"), 4.8, 2, 41.403629, 2.174356, "Basílica projetada por Antoni Gaudí.", "Barcelona, Espanha", "barcelona", "espanha", "spain", "sagrada familia"),
            local("catalogo-barcelona-park-guell", "Park Güell", "08024 Barcelona, Espanha", "Parque", List.of("park", "tourist_attraction"), 4.4, 1, 41.414495, 2.152700, "Parque monumental de Gaudí com vista para Barcelona.", "Barcelona, Espanha", "barcelona", "espanha", "spain", "park guell", "parque guell"),
            local("catalogo-barcelona-casa-batllo", "Casa Batlló", "Passeig de Gràcia 43, 08007 Barcelona, Espanha", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.6, 2, 41.391640, 2.165000, "Obra modernista de Antoni Gaudí no Passeig de Gràcia.", "Barcelona, Espanha", "barcelona", "espanha", "spain", "casa batllo"),
            local("catalogo-barcelona-gotico", "Bairro Gótico de Barcelona", "Barri Gòtic, 08002 Barcelona, Espanha", "Ponto turístico", List.of("tourist_attraction", "historical_landmark"), 4.7, 0, 41.383900, 2.176000, "Área histórica com ruas estreitas e praças medievais.", "Barcelona, Espanha", "barcelona", "espanha", "spain", "bairro gotico", "gothic quarter"),

            // Japão — Tóquio
            local("catalogo-toquio-sensoji", "Templo Sensō-ji", "2 Chome-3-1 Asakusa, Taito City, Tokyo 111-0032, Japão", "Templo", List.of("tourist_attraction", "place_of_worship"), 4.5, 0, 35.714765, 139.796655, "Templo budista histórico no bairro de Asakusa.", "Tóquio, Japão", "toquio", "tokyo", "japao", "japan", "senso ji", "asakusa"),
            local("catalogo-toquio-skytree", "Tokyo Skytree", "1 Chome-1-2 Oshiage, Sumida City, Tokyo 131-0045, Japão", "Ponto turístico", List.of("tourist_attraction", "observation_deck"), 4.4, 2, 35.710062, 139.810700, "Torre de observação e telecomunicações de Tóquio.", "Tóquio, Japão", "toquio", "tokyo", "japao", "japan", "tokyo skytree"),
            local("catalogo-toquio-shibuya", "Shibuya Crossing", "2 Chome-24-1 Shibuya, Shibuya City, Tokyo 150-0002, Japão", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.5, 0, 35.659500, 139.700500, "Travessia movimentada e símbolo da região de Shibuya.", "Tóquio, Japão", "toquio", "tokyo", "japao", "japan", "shibuya crossing"),
            local("catalogo-toquio-meiji", "Santuário Meiji", "1-1 Yoyogikamizonocho, Shibuya City, Tokyo 151-8557, Japão", "Templo", List.of("tourist_attraction", "place_of_worship"), 4.6, 0, 35.676397, 139.699325, "Santuário cercado por uma área arborizada no centro de Tóquio.", "Tóquio, Japão", "toquio", "tokyo", "japao", "japan", "meiji jingu"),

            // Tailândia — Bangkok
            local("catalogo-bangkok-grand-palace", "Grand Palace de Bangkok", "Na Phra Lan Road, Phra Borom Maha Ratchawang, Bangkok 10200, Tailândia", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.5, 1, 13.750000, 100.491400, "Complexo histórico às margens do rio Chao Phraya.", "Bangkok, Tailândia", "bangkok", "tailandia", "thailand", "grand palace", "palacio real"),
            local("catalogo-bangkok-wat-arun", "Wat Arun", "158 Thanon Wang Doem, Wat Arun, Bangkok Yai, Bangkok 10600, Tailândia", "Templo", List.of("tourist_attraction", "place_of_worship"), 4.6, 0, 13.743700, 100.488900, "Templo conhecido pela torre decorada à margem do Chao Phraya.", "Bangkok, Tailândia", "bangkok", "tailandia", "thailand", "wat arun"),
            local("catalogo-bangkok-wat-pho", "Wat Pho", "2 Sanam Chai Road, Phra Borom Maha Ratchawang, Bangkok 10200, Tailândia", "Templo", List.of("tourist_attraction", "place_of_worship"), 4.6, 0, 13.746500, 100.492700, "Templo histórico conhecido pelo Buda reclinado.", "Bangkok, Tailândia", "bangkok", "tailandia", "thailand", "wat pho"),
            local("catalogo-bangkok-chatuchak", "Mercado de Chatuchak", "587/10 Kamphaeng Phet 2 Road, Bangkok 10900, Tailândia", "Mercado", List.of("tourist_attraction", "shopping_mall"), 4.4, 0, 13.799900, 100.550700, "Grande mercado com artesanato, comida e comércio local.", "Bangkok, Tailândia", "bangkok", "tailandia", "thailand", "chatuchak market"),

            // México — Cancún e Cidade do México
            local("catalogo-cancun-playa-delfines", "Playa Delfines", "Punta Nizuc, 77500 Cancún, Quintana Roo, México", "Praia", List.of("beach", "natural_feature"), 4.7, 0, 21.060800, -86.782300, "Praia pública conhecida pela faixa de areia e mar do Caribe.", "Cancún, México", "cancun", "méxico", "mexico", "playa delfines"),
            local("catalogo-cancun-chichen-itza", "Chichén Itzá", "Yucatán 97751, México", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.8, 1, 20.684300, -88.567800, "Sítio arqueológico maia no estado de Yucatán.", "Cancún, México", "cancun", "méxico", "mexico", "chichen itza"),
            local("catalogo-cdmx-zocalo", "Zócalo da Cidade do México", "Plaza de la Constitución, Centro, Cuauhtémoc, 06000 Ciudad de México, México", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.7, 0, 19.432600, -99.133200, "Praça central e referência histórica da Cidade do México.", "Cidade do México, México", "cidade do mexico", "mexico city", "méxico", "mexico", "zocalo"),
            local("catalogo-cdmx-frida", "Museu Frida Kahlo", "Londres 247, Del Carmen, Coyoacán, 04100 Ciudad de México, México", "Museu", List.of("museum", "tourist_attraction"), 4.5, 2, 19.355300, -99.162700, "Casa-museu dedicada à vida e à obra de Frida Kahlo.", "Cidade do México, México", "cidade do mexico", "mexico city", "méxico", "mexico", "frida kahlo", "casa azul"),

            // Grécia — Atenas
            local("catalogo-atenas-acropole", "Acrópole de Atenas", "Athens 10558, Grécia", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.8, 2, 37.971500, 23.725700, "Complexo arqueológico no alto da colina de Atenas.", "Atenas, Grécia", "atenas", "athens", "grecia", "greece", "acropole", "acropolis"),
            local("catalogo-atenas-partenon", "Partenon", "Acropolis, Athens 10558, Grécia", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.8, 1, 37.971500, 23.726700, "Templo histórico localizado na Acrópole.", "Atenas, Grécia", "atenas", "athens", "grecia", "greece", "partenon", "parthenon"),
            local("catalogo-atenas-museu-acropole", "Museu da Acrópole", "Dionysiou Areopagitou 15, Athens 11742, Grécia", "Museu", List.of("museum", "tourist_attraction"), 4.7, 2, 37.968400, 23.728500, "Museu arqueológico dedicado à Acrópole de Atenas.", "Atenas, Grécia", "atenas", "athens", "grecia", "greece", "museu da acropole", "acropolis museum"),
            local("catalogo-atenas-plaka", "Plaka", "Plaka, Athens 10558, Grécia", "Ponto turístico", List.of("tourist_attraction", "neighborhood"), 4.6, 0, 37.972000, 23.730000, "Bairro histórico aos pés da Acrópole.", "Atenas, Grécia", "atenas", "athens", "grecia", "greece", "plaka"),

            // Emirados Árabes Unidos — Dubai
            local("catalogo-dubai-burj-khalifa", "Burj Khalifa", "1 Sheikh Mohammed bin Rashid Boulevard, Dubai, Emirados Árabes Unidos", "Ponto turístico", List.of("tourist_attraction", "observation_deck"), 4.7, 2, 25.197200, 55.274400, "Arranha-céu e mirante no centro de Dubai.", "Dubai, Emirados Árabes Unidos", "dubai", "emirados arabes", "uae", "burj khalifa"),
            local("catalogo-dubai-mall", "Dubai Mall", "Financial Center Road, Downtown Dubai, Emirados Árabes Unidos", "Shopping", List.of("shopping_mall", "tourist_attraction"), 4.7, 2, 25.198500, 55.279600, "Grande centro de compras próximo ao Burj Khalifa.", "Dubai, Emirados Árabes Unidos", "dubai", "emirados arabes", "uae", "dubai mall"),
            local("catalogo-dubai-futuro", "Museu do Futuro", "1 Museum of the Future Street, Dubai, Emirados Árabes Unidos", "Museu", List.of("museum", "tourist_attraction"), 4.5, 2, 25.219600, 55.281900, "Museu de tecnologia e experiências imersivas.", "Dubai, Emirados Árabes Unidos", "dubai", "emirados arabes", "uae", "museum of the future", "museu do futuro"),
            local("catalogo-dubai-palm", "Palm Jumeirah", "Palm Jumeirah, Dubai, Emirados Árabes Unidos", "Ponto turístico", List.of("tourist_attraction", "natural_feature"), 4.6, 0, 25.112400, 55.139000, "Arquipélago artificial em forma de palmeira.", "Dubai, Emirados Árabes Unidos", "dubai", "emirados arabes", "uae", "palm jumeirah"),

            // Turquia — Istambul
            local("catalogo-istambul-hagia-sophia", "Basílica de Santa Sofia", "Ayasofya Meydanı, Sultan Ahmet, 34122 Fatih/Istanbul, Turquia", "Lugar histórico", List.of("historical_landmark", "place_of_worship"), 4.7, 1, 41.008600, 28.980200, "Edifício histórico na praça de Sultanahmet.", "Istambul, Turquia", "istambul", "istanbul", "turquia", "turkey", "santa sofia", "hagia sophia"),
            local("catalogo-istambul-mesquita-azul", "Mesquita Azul", "Atmeydanı No:7, 34122 Fatih/Istanbul, Turquia", "Lugar histórico", List.of("historical_landmark", "place_of_worship"), 4.7, 0, 41.005400, 28.976800, "Mesquita histórica conhecida pelos azulejos azuis.", "Istambul, Turquia", "istambul", "istanbul", "turquia", "turkey", "mesquita azul", "blue mosque"),
            local("catalogo-istambul-topkapi", "Palácio de Topkapi", "Cankurtaran, 34122 Fatih/Istanbul, Turquia", "Museu", List.of("museum", "historical_landmark"), 4.6, 1, 41.011500, 28.983300, "Palácio histórico com coleções do período otomano.", "Istambul, Turquia", "istambul", "istanbul", "turquia", "turkey", "topkapi palace"),
            local("catalogo-istambul-grand-bazaar", "Grande Bazar de Istambul", "Beyazıt, 34126 Fatih/Istanbul, Turquia", "Mercado", List.of("shopping_mall", "tourist_attraction"), 4.4, 0, 41.010700, 28.968000, "Mercado histórico coberto no centro de Istambul.", "Istambul, Turquia", "istambul", "istanbul", "turquia", "turkey", "grand bazaar", "grande bazar"),

            // Austrália — Sydney
            local("catalogo-sydney-opera", "Sydney Opera House", "Bennelong Point, Sydney NSW 2000, Austrália", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.7, 2, -33.856800, 151.215300, "Centro cultural e símbolo arquitetônico de Sydney.", "Sydney, Austrália", "sydney", "australia", "australia", "opera house"),
            local("catalogo-sydney-harbour-bridge", "Sydney Harbour Bridge", "Sydney Harbour Bridge, Sydney NSW, Austrália", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.8, 0, -33.852300, 151.210800, "Ponte histórica com vista para a baía de Sydney.", "Sydney, Austrália", "sydney", "australia", "ponte de sydney", "harbour bridge"),
            local("catalogo-sydney-bondi", "Bondi Beach", "Queen Elizabeth Drive, Bondi Beach NSW 2026, Austrália", "Praia", List.of("beach", "tourist_attraction"), 4.6, 0, -33.891500, 151.276700, "Praia urbana famosa pela orla e pelo surfe.", "Sydney, Austrália", "sydney", "australia", "bondi", "bondi beach"),
            local("catalogo-sydney-jardim", "Royal Botanic Garden Sydney", "Mrs Macquaries Road, Sydney NSW 2000, Austrália", "Parque", List.of("park", "tourist_attraction"), 4.7, 0, -33.864200, 151.216600, "Jardim botânico com vista para a Opera House e a baía.", "Sydney, Austrália", "sydney", "australia", "royal botanic garden"),

            // Canadá — Toronto e Vancouver
            local("catalogo-toronto-cn-tower", "CN Tower", "301 Front Street West, Toronto, ON M5V 2T6, Canadá", "Ponto turístico", List.of("tourist_attraction", "observation_deck"), 4.7, 2, 43.642600, -79.387100, "Torre de observação e símbolo de Toronto.", "Toronto, Canadá", "toronto", "canada", "canadá", "cn tower"),
            local("catalogo-toronto-casasoma", "Casa Loma", "1 Austin Terrace, Toronto, ON M5R 1X8, Canadá", "Lugar histórico", List.of("historical_landmark", "tourist_attraction"), 4.5, 2, 43.678000, -79.409400, "Castelo histórico e museu em Toronto.", "Toronto, Canadá", "toronto", "canada", "canadá", "casa loma"),
            local("catalogo-vancouver-stanley", "Stanley Park", "Vancouver, BC V6G 1Z4, Canadá", "Parque", List.of("park", "tourist_attraction"), 4.8, 0, 49.304300, -123.144300, "Parque urbano com trilhas e vista para a baía de Vancouver.", "Vancouver, Canadá", "vancouver", "canada", "canadá", "stanley park"),
            local("catalogo-vancouver-granville", "Granville Island", "1661 Duranleau Street, Vancouver, BC V6H 3S3, Canadá", "Mercado", List.of("tourist_attraction", "shopping_mall"), 4.6, 1, 49.271200, -123.134000, "Mercado público e área cultural de Vancouver.", "Vancouver, Canadá", "vancouver", "canada", "canadá", "granville island"),

            // Países Baixos — Amsterdã
            local("catalogo-amsterda-rijksmuseum", "Rijksmuseum", "Museumstraat 1, 1071 XX Amsterdam, Países Baixos", "Museu", List.of("museum", "tourist_attraction"), 4.7, 2, 52.360000, 4.885200, "Museu nacional de arte e história dos Países Baixos.", "Amsterdã, Países Baixos", "amsterda", "amsterdam", "paises baixos", "netherlands", "rijksmuseum"),
            local("catalogo-amsterda-anne-frank", "Casa de Anne Frank", "Westermarkt 20, 1016 GV Amsterdam, Países Baixos", "Museu", List.of("museum", "historical_landmark"), 4.5, 2, 52.375200, 4.884000, "Casa-museu dedicada à história de Anne Frank.", "Amsterdã, Países Baixos", "amsterda", "amsterdam", "paises baixos", "netherlands", "anne frank house"),
            local("catalogo-amsterda-van-gogh", "Museu Van Gogh", "Museumplein 6, 1071 DJ Amsterdam, Países Baixos", "Museu", List.of("museum", "art_gallery"), 4.6, 2, 52.358400, 4.881100, "Museu dedicado à obra de Vincent van Gogh.", "Amsterdã, Países Baixos", "amsterda", "amsterdam", "paises baixos", "netherlands", "van gogh museum"),
            local("catalogo-amsterda-dam", "Praça Dam", "Dam, 1012 JL Amsterdam, Países Baixos", "Ponto turístico", List.of("tourist_attraction", "landmark"), 4.5, 0, 52.373100, 4.892600, "Praça central e referência histórica de Amsterdã.", "Amsterdã, Países Baixos", "amsterda", "amsterdam", "paises baixos", "netherlands", "dam square", "praca dam")
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
