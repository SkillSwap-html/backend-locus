# API do Locus

Base local: `http://localhost:8080`

Quando `SECURITY_ENABLED=true`, as chamadas protegidas devem enviar:

```http
Authorization: Bearer <token-firebase>
```

## Saúde e teste

| Método | Rota | Finalidade |
|---|---|---|
| GET | `/actuator/health` | Verificar a saúde da aplicação |
| GET | `/api/teste` | Verificar a resposta básica da API |

## Usuários e administração

| Método | Rota | Finalidade |
|---|---|---|
| POST | `/api/usuarios` | Cadastrar usuário |
| GET | `/api/usuarios` | Listar usuários |
| GET | `/api/usuarios/{id}` | Consultar usuário |
| GET | `/api/admin/usuarios` | Listar usuários como administrador |
| PUT | `/api/admin/usuarios/{usuarioId}/tipo` | Alterar tipo de usuário |

## Viagens, destinos e perfis

| Método | Rota | Finalidade |
|---|---|---|
| POST | `/api/viagens/usuario/{usuarioId}` | Criar viagem |
| GET | `/api/viagens/usuario/{usuarioId}` | Listar viagens do usuário |
| GET | `/api/viagens/{id}` | Consultar viagem |
| POST | `/api/destinos/viagem/{viagemId}` | Adicionar destino |
| GET | `/api/destinos/viagem/{viagemId}` | Listar destinos |
| POST | `/api/perfis/usuario/{usuarioId}` | Criar perfil de viajante |
| GET | `/api/perfis/usuario/{usuarioId}` | Consultar perfil |
| POST | `/api/perfis-veiculo/perfil/{perfilId}` | Criar perfil de veículo |
| POST | `/api/preferencias-transporte/perfil/{perfilId}` | Registrar preferência de transporte |
| POST | `/api/preferencias-viagem/perfil/{perfilId}` | Registrar preferência de viagem |

## Lugares e roteiros

| Método | Rota | Finalidade |
|---|---|---|
| POST | `/api/lugares/destino/{destinoId}` | Cadastrar lugar |
| GET | `/api/lugares/destino/{destinoId}` | Listar lugares |
| PUT | `/api/lugares/{id}` | Atualizar lugar |
| POST | `/api/dias-roteiro/viagem/{viagemId}` | Criar dia de roteiro |
| GET | `/api/dias-roteiro/viagem/{viagemId}` | Listar dias |
| POST | `/api/itens-roteiro/dia/{diaRoteiroId}` | Adicionar atividade |
| PUT | `/api/itens-roteiro/{itemId}` | Atualizar atividade |
| PUT | `/api/itens-roteiro/dia/{diaRoteiroId}/reordenar` | Reordenar atividades |
| DELETE | `/api/itens-roteiro/{itemId}` | Remover atividade |
| POST | `/api/versoes-roteiro/viagem/{viagemId}` | Criar versão do roteiro |
| GET | `/api/versoes-roteiro/comparar` | Comparar versões |

## Orçamento e transporte

| Método | Rota | Finalidade |
|---|---|---|
| POST | `/api/estimativas-rota/viagem/{viagemId}` | Registrar estimativa de rota |
| GET | `/api/estimativas-rota/viagem/{viagemId}` | Listar estimativas |
| POST | `/api/itens-orcamento/viagem/{viagemId}` | Adicionar item ao orçamento |
| GET | `/api/orcamento/viagem/{viagemId}/resumo` | Consultar resumo do orçamento |
| POST | `/api/limites-orcamento/viagem/{viagemId}` | Criar limite de categoria |
| PUT | `/api/limites-orcamento/{limiteId}` | Atualizar limite |
| POST | `/api/precos/snapshots` | Registrar preço observado |
| GET | `/api/precos/historico` | Consultar histórico |
| GET | `/api/precos/oportunidade` | Consultar oportunidade de preço |

## Reserva, pagamento, relatório e compartilhamento

| Método | Rota | Finalidade |
|---|---|---|
| POST | `/api/reservas-simuladas/viagem/{viagemId}` | Criar reserva simulada |
| POST | `/api/reservas-simuladas/{reservaId}/preparar-pagamento` | Preparar pagamento |
| POST | `/api/reservas-simuladas/{reservaId}/confirmar` | Confirmar simulação |
| POST | `/api/pagamentos-simulados/reserva/{reservaId}/iniciar` | Iniciar pagamento simulado |
| POST | `/api/pagamentos-simulados/{pagamentoId}/concluir` | Concluir pagamento |
| POST | `/api/pagamentos-simulados/{pagamentoId}/interromper` | Interromper pagamento |
| POST | `/api/relatorios/reserva/{reservaId}` | Gerar relatório PDF |
| GET | `/api/relatorios/{relatorioId}/pdf` | Baixar PDF |
| POST | `/api/links-compartilhamento/viagem/{viagemId}` | Criar link de compartilhamento |
| PUT | `/api/links-compartilhamento/{linkId}/revogar` | Revogar link |
| POST | `/api/permissoes-compartilhamento/viagem/{viagemId}/usuario/{usuarioId}` | Conceder permissão |

## Integrações externas

As rotas abaixo são adaptadores do backend para os serviços definidos na ERS. Elas só fazem chamadas externas quando a integração correspondente estiver habilitada e com credencial configurada.

| Método | Rota | Serviço |
|---|---|---|
| POST | `/api/integracoes/gemini/conteudo` | Gemini API |
| POST | `/api/integracoes/google/places/busca-textual` | Google Places API |
| POST | `/api/integracoes/google/places/autocomplete` | Google Places API |
| GET | `/api/integracoes/google/places/{placeId}` | Google Places API |
| POST | `/api/integracoes/google/routes` | Google Routes API |
| GET | `/api/integracoes/google/street-view/metadata` | Google Street View API |
| GET | `/api/integracoes/amadeus/voos` | Amadeus Flight Offers Search |
| POST | `/api/integracoes/amadeus/voos/preco` | Amadeus Flight Offers Price |
| GET | `/api/integracoes/amadeus/hoteis` | Amadeus Hotel List |
| GET | `/api/integracoes/amadeus/hoteis/ofertas` | Amadeus Hotel Search |

Exemplo de corpo para o Gemini:

```json
{
  "prompt": "Sugira opções de viagem para três dias, com pouco deslocamento e foco em natureza."
}
```

Exemplo de corpo para o Places Text Search:

```json
{
  "textQuery": "museus em Salvador",
  "languageCode": "pt-BR"
}
```

O endpoint de Routes recebe o corpo oficial da Routes API. O endpoint de Amadeus para preço recebe uma oferta retornada pelo Flight Offers Search. As reservas e pagamentos continuam sendo simulados dentro do Locus.

## Observação

Os parâmetros `usuarioSolicitanteId` ainda fazem parte da compatibilidade dos controllers existentes. A autenticação por token protege o acesso quando habilitada, mas a substituição gradual desses parâmetros pelo usuário autenticado deve ser feita junto com a integração definitiva do frontend.
