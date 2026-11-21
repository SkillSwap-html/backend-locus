package br.com.locus.integracao;

import br.com.locus.integracao.gemini.GeminiClient;
import br.com.locus.integracao.groq.GroqClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class AssistenteViagemClient {

    private final GeminiClient geminiClient;
    private final GroqClient groqClient;

    public AssistenteViagemClient(
            GeminiClient geminiClient,
            GroqClient groqClient) {
        this.geminiClient = geminiClient;
        this.groqClient = groqClient;
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
            throw new ExternalApiException(
                    "Assistente Locus",
                    503,
                    "Gemini e Groq não responderam. "
                            + falhaGemini.getMessage()
            );
        }
    }
}
