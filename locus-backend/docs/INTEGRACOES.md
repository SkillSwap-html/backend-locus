# Integrações externas

Divisão das integrações previstas na ERS e do que o backend faz com cada uma.

| Integração | Uso no Locus | Configuração principal | Situação no backend |
|---|---|---|---|
| Firebase Authentication | Validar token, emissor, expiração e roles | `FIREBASE_PROJECT_ID`, `SECURITY_ENABLED` | Validação JWT preparada |
| Maps JavaScript API | Mapa exibido no frontend | `GOOGLE_MAPS_JAVASCRIPT_API_KEY` | Chave prevista para o frontend |
| Places API | Autocomplete, busca textual e detalhes de lugares | `GOOGLE_PLACES_*` | Adaptador REST disponível |
| Street View API | Metadata de panorama e verificação de cobertura | `GOOGLE_STREET_VIEW_*` | Adaptador REST disponível |
| Routes API | Distância, duração e rota entre pontos | `GOOGLE_ROUTES_*` | Adaptador REST disponível |
| Gemini API | Conversa, sugestões e explicação das alternativas | `GEMINI_*` | Adaptador REST disponível |
| Groq API | Fallback do assistente quando o Gemini estiver indisponível | `GROQ_*` | Adaptador REST disponível |
| Ofertas de voos | Busca de ofertas de voo | `AMADEUS_MODE`, `AMADEUS_*` | Catálogo local simulado por padrão; Amadeus real opcional |
| Preço de voo | Conferência de uma oferta de voo | `AMADEUS_MODE`, `AMADEUS_*` | Mesmo contrato da consulta de preço |
| Lista de hotéis | Lista de hotéis por cidade | `AMADEUS_MODE`, `AMADEUS_*` | Catálogo local simulado por padrão; Amadeus real opcional |
| Ofertas de hospedagem | Busca de ofertas de hospedagem | `AMADEUS_MODE`, `AMADEUS_*` | Mesmo contrato da consulta de ofertas |

As rotas REST desses adaptadores estão documentadas em `docs/API.md`. Elas retornam a resposta do provedor para que os módulos de negócio possam normalizar, salvar, auditar e exibir os dados. As credenciais não ficam no código.

As rotas de voos e hospedagem usam `AMADEUS_MODE=mock` por padrão. Nesse modo, o backend entrega um catálogo simulado determinístico do próprio Locus, com a mesma estrutura principal esperada pelo frontend, sem inventar preços por meio de um modelo de linguagem. A interface identifica esses valores como `Locus (simulado)`. Para desativar completamente essas rotas, use `AMADEUS_MODE=disabled`.

Quando `GEMINI_WEB_SEARCH_ENABLED=true`, o Gemini pode consultar a busca do Google para apoiar respostas atuais. Essas respostas continuam sendo recomendações sujeitas a conferência: o Locus não transforma pesquisa na web em disponibilidade garantida, reserva real ou preço vinculante. Se o Gemini falhar, o endpoint do assistente tenta o Groq configurado em `GROQ_*`.

A reserva, o pagamento e a confirmação continuam locais e simulados. Nenhum adaptador desta camada cria compra, emite passagem, bloqueia quarto ou cobra cartão.

## Ativação local

1. Copiar `.env.example` para um arquivo `.env` local.
2. Preencher somente as credenciais dos serviços que serão utilizados.
3. Alterar a variável `*_ENABLED` correspondente para `true`.
4. Manter as chaves restritas por aplicação, domínio, quota e ambiente no painel do provedor.
5. Fazer uma chamada de teste e verifique a resposta antes de considerar a integração validada.

No ambiente local, o modo `mock` permite testar o fluxo de ofertas sem uma conta Amadeus. As respostas simuladas devem aparecer na interface como simulação e não representam disponibilidade ou preço reservável.
