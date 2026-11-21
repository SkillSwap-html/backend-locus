# Locus Backend

Backend do Locus, plataforma de apoio ao planejamento de viagens. O projeto foi organizado com Java 21, Spring Boot, PostgreSQL e Redis.

## O que já está no projeto

- cadastro de usuários, perfis e preferências;
- viagens, destinos, lugares e fontes de informação;
- roteiros, dias, atividades, deslocamentos e versões;
- orçamento, limites, estimativas de transporte e histórico de preços;
- reservas e pagamentos simulados;
- compartilhamento de viagens e permissões;
- notificações, auditoria e relatórios em PDF;
- controle de acesso administrativo;
- migrations do banco com Flyway;
- configuração de CORS por variável de ambiente;
- autenticação por token Firebase quando a segurança estiver habilitada.
- adaptadores para Places, Routes, Street View, Gemini e Groq;
- catálogo local simulado de voos e hotéis, mantendo as rotas compatíveis com o contrato previsto para o Amadeus.

## Requisitos

- Java 21;
- Docker Desktop, ou PostgreSQL e Redis instalados localmente;
- Maven Wrapper incluído no projeto.

## Subir PostgreSQL e Redis

Na pasta do projeto:

```bash
docker compose up -d
```

O PostgreSQL será disponibilizado na porta `5432` e o Redis na porta `6379`.

## Variáveis de ambiente

Copie `.env.example` para um arquivo local ou configure as variáveis no terminal. O arquivo `.env.example` não contém credenciais reais.

Variáveis principais:

- `DB_URL`: URL JDBC do PostgreSQL;
- `DB_USERNAME`: usuário do banco;
- `DB_PASSWORD`: senha do banco;
- `REDIS_HOST` e `REDIS_PORT`: endereço do Redis;
- `FLYWAY_ENABLED`: habilita as migrations;
- `SECURITY_ENABLED`: habilita a validação de tokens;
- `FIREBASE_PROJECT_ID`: projeto Firebase usado para validar tokens;
- `CORS_ALLOWED_ORIGINS`: origens autorizadas para o frontend.

As variáveis das integrações externas também estão no `.env.example`:

- Firebase Authentication: `FIREBASE_PROJECT_ID` no backend; `FIREBASE_WEB_API_KEY` fica disponível para a configuração do frontend;
- Google Maps JavaScript API: `GOOGLE_MAPS_JAVASCRIPT_API_KEY`;
- Google Places API: `GOOGLE_PLACES_API_KEY`;
- Google Routes API: `GOOGLE_ROUTES_API_KEY`;
- Google Street View API: `GOOGLE_STREET_VIEW_API_KEY`;
- Gemini API: `GEMINI_API_KEY`, `GEMINI_MODEL` e limites de geração;
- pesquisa web opcional do Gemini: `GEMINI_WEB_SEARCH_ENABLED`;
- Groq como fallback do assistente: `GROQ_ENABLED`, `GROQ_API_KEY` e `GROQ_MODEL`;
- catálogo simulado do Locus: `AMADEUS_MODE=mock` (padrão, sem chave externa).

Cada integração possui uma variável `*_ENABLED`, uma URL-base e um timeout. As integrações começam desligadas para que a aplicação local não dependa de credenciais. Depois de preencher as chaves, altere somente as integrações que serão usadas para `true`.

Por padrão, a segurança fica desligada para facilitar o desenvolvimento local. Em ambiente de produção, use `SECURITY_ENABLED=true` e informe `FIREBASE_PROJECT_ID`.

## Banco de dados

As migrations ficam em:

```text
src/main/resources/db/migration
```

A migration `V1__baseline_locus.sql` cria as tabelas correspondentes às entidades JPA e os índices principais. O Hibernate fica em modo `validate`, portanto ele verifica a estrutura sem alterá-la.

Para um banco de desenvolvimento, os dados opcionais ficam em:

```text
src/main/resources/db/seed/seed-dev.sql
```

Esse arquivo não é executado automaticamente. Use-o somente em banco local.

## Executar

No Windows:

```powershell
$env:DB_PASSWORD="sua_senha"
$env:SPRING_PROFILES_ACTIVE="local"
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
export DB_PASSWORD="sua_senha"
export SPRING_PROFILES_ACTIVE=local
./mvnw spring-boot:run
```

O health check fica disponível em:

```text
GET http://localhost:8080/actuator/health
```

Para gerar uma imagem da aplicação depois do build:

```bash
./mvnw clean package -DskipTests
docker build -t locus-backend .
```

## Segurança

Quando `SECURITY_ENABLED=true`, as rotas protegidas exigem um Bearer Token emitido pelo Firebase Authentication. O backend valida a assinatura e o emissor do token usando os certificados públicos do Firebase.

As roles podem ser informadas nos custom claims do token pelo campo `role`, como `ADMINISTRADOR` ou `RESPONSAVEL_TECNICO`. O claim booleano `admin=true` também concede a role de administrador.

O cadastro inicial de usuário continua público para permitir a criação do perfil depois da autenticação no frontend. As demais rotas exigem autenticação.

## Testes

Para executar os testes locais:

```powershell
$env:MAVEN_USER_HOME=".maven"
.\mvnw.cmd test
```

Os testes usam H2 em memória. O teste que depende de um Redis real fica desabilitado por padrão. Para executá-lo, suba o Redis e use:

```powershell
$env:RUN_REDIS_TESTS="true"
.\mvnw.cmd test
```

## Organização da API

Os controllers estão separados pelos módulos do domínio. O resumo dos endpoints fica em `docs/API.md`.

As rotas de integração ficam em `/api/integracoes`. Elas encaminham as consultas técnicas para os provedores configurados, sem criar reservas reais, emitir passagens, cobrar pagamentos ou redirecionar o usuário para uma compra.

O mapa de integrações, as variáveis utilizadas e a forma de ativação estão em `docs/INTEGRACOES.md`.

## Limites atuais

As integrações com Gemini, Groq, Google e Firebase dependem das credenciais, quotas e contratos reais de cada serviço. Os adaptadores e as configurações estão no projeto, mas uma integração externa só deve ser considerada validada depois de executar uma chamada real com credenciais válidas. Quando um provedor estiver desligado, o backend retorna uma resposta de serviço indisponível; a única simulação prevista é o catálogo determinístico de ofertas do próprio Locus.
