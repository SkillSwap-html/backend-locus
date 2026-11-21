package br.com.locus.integracao;

import br.com.locus.integracao.gemini.GeminiClient;
import br.com.locus.integracao.groq.GroqClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class AssistenteViagemClient {

    private final GeminiClient geminiClient;
    private final GroqClient groqClient;
    private final ObjectMapper objectMapper;

    public AssistenteViagemClient(
            GeminiClient geminiClient,
            GroqClient groqClient,
            ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.groqClient = groqClient;
        this.objectMapper = objectMapper;
    }

    public JsonNode gerar(String prompt) {
        RuntimeException falhaGemini;
        try {
            return geminiClient.generateContent(prompt);
        } catch (RuntimeException exception) {
            falhaGemini = exception;
        }

        try {
            return groqClient.generateContent(prompt);
        } catch (RuntimeException falhaGroq) {
            return respostaDeContingencia(prompt);
        }
    }

    private JsonNode respostaDeContingencia(String prompt) {
        String destino = extrairDestino(prompt);
        String consulta = destino.isBlank()
                ? "pontos turísticos e atrações"
                : "pontos turísticos, praias e atrações em " + destino;
        ObjectNode resposta = objectMapper.createObjectNode();
        resposta.put(
                "resposta",
                destino.isBlank()
                        ? "Posso buscar locais reais assim que você informar o destino da viagem."
                        : "Entendi. Vou buscar locais reais no destino, organizar por proximidade e separar as sugestões por dia."
        );
        resposta.put("etapa", "roteiro");
        resposta.putArray("consultas").add(consulta);
        resposta.putArray("recomendacoes");
        return resposta;
    }

    private String extrairDestino(String prompt) {
        String marcador = "Destino obrigatório desta viagem: ";
        int inicio = prompt.indexOf(marcador);
        if (inicio < 0) {
            return "";
        }
        String trecho = prompt.substring(inicio + marcador.length());
        int fim = trecho.indexOf('.');
        return (fim >= 0 ? trecho.substring(0, fim) : trecho).trim();
    }
}
