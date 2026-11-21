INSERT INTO usuarios (
    firebase_uid,
    nome,
    email,
    termos_aceitos,
    tipo_usuario
)
VALUES (
    'dev-locus-uid',
    'Usuário de Desenvolvimento',
    'locusgg@gmail.com',
    TRUE,
    'ADMINISTRADOR'
)
ON CONFLICT (email) DO NOTHING;

INSERT INTO viagens (
    usuario_id,
    titulo,
    origem,
    destino,
    data_inicio,
    data_fim,
    modo_transporte,
    tipo_orcamento,
    viabilidade_transporte_terrestre
)
SELECT
    id,
    'Viagem de demonstração',
    'Salvador, BA',
    'Rio de Janeiro, RJ',
    '2026-12-01',
    '2026-12-05',
    'AVIAO',
    'ABERTO',
    'NAO_VERIFICADA'
FROM usuarios
WHERE email = 'dev@locus.local'
  AND NOT EXISTS (
      SELECT 1
      FROM viagens
      WHERE titulo = 'Viagem de demonstração'
  );

INSERT INTO destinos (
    viagem_id,
    cidade,
    estado,
    pais
)
SELECT
    id,
    'Rio de Janeiro',
    'RJ',
    'Brasil'
FROM viagens
WHERE titulo = 'Viagem de demonstração'
  AND NOT EXISTS (
      SELECT 1
      FROM destinos
      WHERE viagem_id = viagens.id
  );

INSERT INTO perfis_viajante (
    usuario_id,
    idade,
    ritmo_viagem,
    interesses,
    mobilidade,
    preferencia_hospedagem
)
SELECT
    id,
    25,
    'EQUILIBRADO',
    'praias, cultura, gastronomia e fotografia',
    'SEM_RESTRICAO',
    'HOTEL'
FROM usuarios
WHERE email = 'dev@locus.local'
  AND NOT EXISTS (
      SELECT 1
      FROM perfis_viajante
      WHERE usuario_id = usuarios.id
  );

INSERT INTO preferencias_transporte (
    perfil_viajante_id,
    modo_transporte,
    prioridade
)
SELECT
    id,
    'AVIAO',
    'DESEJAVEL'
FROM perfis_viajante
WHERE usuario_id = (
    SELECT id
    FROM usuarios
    WHERE email = 'dev@locus.local'
)
AND NOT EXISTS (
    SELECT 1
    FROM preferencias_transporte
    WHERE perfil_viajante_id = perfis_viajante.id
);

INSERT INTO preferencias_viagem (
    perfil_viajante_id,
    origem_preferida,
    destino_preferido,
    tipo_orcamento,
    valor_orcamento
)
SELECT
    id,
    'Salvador, BA',
    'Rio de Janeiro, RJ',
    'ABERTO',
    NULL
FROM perfis_viajante
WHERE usuario_id = (
    SELECT id
    FROM usuarios
    WHERE email = 'dev@locus.local'
)
AND NOT EXISTS (
    SELECT 1
    FROM preferencias_viagem
    WHERE perfil_viajante_id = perfis_viajante.id
);

INSERT INTO dias_roteiro (
    viagem_id,
    data,
    observacoes
)
SELECT
    id,
    '2026-12-01',
    'Primeiro dia da viagem'
FROM viagens
WHERE titulo = 'Viagem de demonstração'
  AND NOT EXISTS (
      SELECT 1
      FROM dias_roteiro
      WHERE viagem_id = viagens.id
        AND data = '2026-12-01'
  );

INSERT INTO dias_roteiro (
    viagem_id,
    data,
    observacoes
)
SELECT
    id,
    '2026-12-02',
    'Segundo dia da viagem'
FROM viagens
WHERE titulo = 'Viagem de demonstração'
  AND NOT EXISTS (
      SELECT 1
      FROM dias_roteiro
      WHERE viagem_id = viagens.id
        AND data = '2026-12-02'
  );

INSERT INTO itens_orcamento (
    viagem_id,
    categoria,
    descricao,
    valor,
    fonte
)
SELECT
    id,
    'HOSPEDAGEM',
    'Hospedagem demonstrativa',
    850.00,
    'DEMONSTRATIVO'
FROM viagens
WHERE titulo = 'Viagem de demonstração'
  AND NOT EXISTS (
      SELECT 1
      FROM itens_orcamento
      WHERE viagem_id = viagens.id
        AND descricao = 'Hospedagem demonstrativa'
  );

INSERT INTO itens_orcamento (
    viagem_id,
    categoria,
    descricao,
    valor,
    fonte
)
SELECT
    id,
    'ALIMENTACAO',
    'Alimentação estimada',
    500.00,
    'ESTIMATIVA_CALCULADA'
FROM viagens
WHERE titulo = 'Viagem de demonstração'
  AND NOT EXISTS (
      SELECT 1
      FROM itens_orcamento
      WHERE viagem_id = viagens.id
        AND descricao = 'Alimentação estimada'
  );

INSERT INTO limites_orcamento (
    viagem_id,
    categoria,
    valor_limite
)
SELECT
    id,
    'HOSPEDAGEM',
    1200.00
FROM viagens
WHERE titulo = 'Viagem de demonstração'
  AND NOT EXISTS (
      SELECT 1
      FROM limites_orcamento
      WHERE viagem_id = viagens.id
        AND categoria = 'HOSPEDAGEM'
  );

INSERT INTO lugares (
    destino_id,
    nome,
    categoria,
    endereco,
    latitude,
    longitude
)
SELECT
    destinos.id,
    'Cristo Redentor',
    'PONTO_TURISTICO',
    'Parque Nacional da Tijuca, Rio de Janeiro - RJ',
    -22.9519,
    -43.2105
FROM destinos
JOIN viagens
    ON viagens.id = destinos.viagem_id
WHERE viagens.titulo = 'Viagem de demonstração'
  AND NOT EXISTS (
      SELECT 1
      FROM lugares
      WHERE destino_id = destinos.id
        AND nome = 'Cristo Redentor'
  );