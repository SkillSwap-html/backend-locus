CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    firebase_uid VARCHAR(128) NOT NULL UNIQUE,
    nome VARCHAR(160) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    termos_aceitos BOOLEAN NOT NULL,
    tipo_usuario VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS viagens (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    titulo VARCHAR(255) NOT NULL DEFAULT 'Minha próxima viagem',
    origem VARCHAR(255) NOT NULL,
    destino VARCHAR(255) NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    modo_transporte VARCHAR(255),
    tipo_orcamento VARCHAR(255) NOT NULL DEFAULT 'ABERTO',
    viabilidade_transporte_terrestre VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS destinos (
    id BIGSERIAL PRIMARY KEY,
    viagem_id BIGINT NOT NULL REFERENCES viagens(id),
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(255),
    pais VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS perfis_viajante (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuarios(id),
    idade INTEGER,
    ritmo_viagem VARCHAR(255),
    interesses VARCHAR(255),
    mobilidade VARCHAR(255),
    preferencia_hospedagem VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS perfis_veiculo (
    id BIGSERIAL PRIMARY KEY,
    perfil_viajante_id BIGINT NOT NULL UNIQUE REFERENCES perfis_viajante(id),
    tipo_veiculo VARCHAR(255),
    tipo_combustivel VARCHAR(255),
    consumo_medio DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS preferencias_transporte (
    id BIGSERIAL PRIMARY KEY,
    perfil_viajante_id BIGINT NOT NULL UNIQUE REFERENCES perfis_viajante(id),
    modo_transporte VARCHAR(255),
    prioridade VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS preferencias_viagem (
    id BIGSERIAL PRIMARY KEY,
    perfil_viajante_id BIGINT NOT NULL UNIQUE REFERENCES perfis_viajante(id),
    origem_preferida VARCHAR(255),
    destino_preferido VARCHAR(255),
    tipo_orcamento VARCHAR(255),
    valor_orcamento NUMERIC(12,2)
);

CREATE TABLE IF NOT EXISTS lugares (
    id BIGSERIAL PRIMARY KEY,
    destino_id BIGINT NOT NULL REFERENCES destinos(id),
    nome VARCHAR(255) NOT NULL,
    categoria VARCHAR(255),
    endereco VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS fontes_lugar (
    id BIGSERIAL PRIMARY KEY,
    lugar_id BIGINT NOT NULL UNIQUE REFERENCES lugares(id),
    fonte VARCHAR(255) NOT NULL,
    data_consulta TIMESTAMP,
    identificador_externo VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS dias_roteiro (
    id BIGSERIAL PRIMARY KEY,
    viagem_id BIGINT NOT NULL REFERENCES viagens(id),
    data DATE NOT NULL,
    observacoes VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS itens_roteiro (
    id BIGSERIAL PRIMARY KEY,
    versao BIGINT,
    dia_roteiro_id BIGINT NOT NULL REFERENCES dias_roteiro(id),
    lugar_id BIGINT REFERENCES lugares(id),
    titulo VARCHAR(255) NOT NULL,
    horario_inicio TIME,
    duracao_minutos INTEGER,
    ordem INTEGER,
    custo_estimado DOUBLE PRECISION,
    observacoes VARCHAR(255),
    motivo_recomendacao VARCHAR(255),
    favorito BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS deslocamentos_roteiro (
    id BIGSERIAL PRIMARY KEY,
    item_origem_id BIGINT NOT NULL REFERENCES itens_roteiro(id),
    item_destino_id BIGINT NOT NULL REFERENCES itens_roteiro(id),
    duracao_minutos INTEGER,
    margem_minutos INTEGER,
    fonte_estimativa VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS estimativas_rota (
    id BIGSERIAL PRIMARY KEY,
    viagem_id BIGINT NOT NULL REFERENCES viagens(id),
    modo_transporte VARCHAR(255) NOT NULL,
    distancia_km NUMERIC(10,2) NOT NULL,
    duracao_minutos INTEGER NOT NULL,
    custo_combustivel NUMERIC(12,2),
    custo_pedagios NUMERIC(12,2),
    custo_estacionamento NUMERIC(12,2),
    custo_total_estimado NUMERIC(12,2),
    quantidade_passageiros INTEGER,
    custo_por_passageiro NUMERIC(12,2),
    fonte_estimativa VARCHAR(255) NOT NULL,
    data_consulta TIMESTAMP NOT NULL,
    preco_combustivel_litro NUMERIC(10,2),
    consumo_medio_utilizado NUMERIC(10,2)
);

CREATE TABLE IF NOT EXISTS itens_orcamento (
    id BIGSERIAL PRIMARY KEY,
    viagem_id BIGINT NOT NULL REFERENCES viagens(id),
    estimativa_rota_id BIGINT REFERENCES estimativas_rota(id),
    categoria VARCHAR(255) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor NUMERIC(12,2) NOT NULL,
    fonte VARCHAR(255),
    tipo_oferta VARCHAR(255),
    oferta_id BIGINT
);

CREATE TABLE IF NOT EXISTS limites_orcamento (
    id BIGSERIAL PRIMARY KEY,
    viagem_id BIGINT NOT NULL REFERENCES viagens(id),
    categoria VARCHAR(255) NOT NULL,
    valor_limite NUMERIC(12,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservas_simuladas (
    id BIGSERIAL PRIMARY KEY,
    viagem_id BIGINT NOT NULL REFERENCES viagens(id),
    protocolo VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(255) NOT NULL,
    valor_total_simulado NUMERIC(12,2),
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL,
    data_confirmacao TIMESTAMP,
    data_cancelamento TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pagamentos_simulados (
    id BIGSERIAL PRIMARY KEY,
    reserva_simulada_id BIGINT NOT NULL UNIQUE REFERENCES reservas_simuladas(id),
    status VARCHAR(255) NOT NULL,
    valor NUMERIC(12,2) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_conclusao TIMESTAMP,
    data_interrupcao TIMESTAMP
);

CREATE TABLE IF NOT EXISTS relatorios_viagem (
    id BIGSERIAL PRIMARY KEY,
    reserva_simulada_id BIGINT NOT NULL UNIQUE REFERENCES reservas_simuladas(id),
    nome_arquivo VARCHAR(255) NOT NULL,
    arquivo_pdf BYTEA NOT NULL,
    data_geracao TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS downloads_relatorio (
    id BIGSERIAL PRIMARY KEY,
    relatorio_viagem_id BIGINT NOT NULL REFERENCES relatorios_viagem(id),
    data_download TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS links_compartilhamento (
    id BIGSERIAL PRIMARY KEY,
    viagem_id BIGINT NOT NULL REFERENCES viagens(id),
    token VARCHAR(255) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_revogacao TIMESTAMP
);

CREATE TABLE IF NOT EXISTS permissoes_compartilhamento (
    id BIGSERIAL PRIMARY KEY,
    viagem_id BIGINT NOT NULL REFERENCES viagens(id),
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    tipo_permissao VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_revogacao TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notificacoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    tipo VARCHAR(255) NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    mensagem VARCHAR(1000) NOT NULL,
    lida BOOLEAN NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_leitura TIMESTAMP
);

CREATE TABLE IF NOT EXISTS eventos_auditoria (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES usuarios(id),
    acao VARCHAR(255) NOT NULL,
    tipo_recurso VARCHAR(255) NOT NULL,
    recurso_id BIGINT,
    resultado VARCHAR(255) NOT NULL,
    detalhes VARCHAR(1000),
    correlation_id VARCHAR(255) NOT NULL,
    data_evento TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS snapshots_preco (
    id BIGSERIAL PRIMARY KEY,
    tipo_oferta VARCHAR(255) NOT NULL,
    oferta_id BIGINT NOT NULL,
    preco NUMERIC(12,2) NOT NULL,
    moeda VARCHAR(255) NOT NULL,
    fonte VARCHAR(255) NOT NULL,
    data_consulta TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS versoes_roteiro (
    id BIGSERIAL PRIMARY KEY,
    viagem_id BIGINT NOT NULL REFERENCES viagens(id),
    numero_versao INTEGER NOT NULL,
    descricao VARCHAR(255),
    data_criacao TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS itens_versao_roteiro (
    id BIGSERIAL PRIMARY KEY,
    versao_roteiro_id BIGINT NOT NULL REFERENCES versoes_roteiro(id),
    data_dia DATE NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    nome_lugar VARCHAR(255),
    horario_inicio TIME,
    duracao_minutos INTEGER,
    ordem INTEGER,
    custo_estimado DOUBLE PRECISION,
    observacoes VARCHAR(255),
    motivo_recomendacao VARCHAR(255),
    favorito BOOLEAN
);

CREATE INDEX IF NOT EXISTS idx_viagens_usuario
    ON viagens(usuario_id);

CREATE INDEX IF NOT EXISTS idx_destinos_viagem
    ON destinos(viagem_id);

CREATE INDEX IF NOT EXISTS idx_lugares_destino
    ON lugares(destino_id);

CREATE INDEX IF NOT EXISTS idx_dias_roteiro_viagem
    ON dias_roteiro(viagem_id);

CREATE INDEX IF NOT EXISTS idx_itens_roteiro_dia
    ON itens_roteiro(dia_roteiro_id);

CREATE INDEX IF NOT EXISTS idx_estimativas_rota_viagem
    ON estimativas_rota(viagem_id);

CREATE INDEX IF NOT EXISTS idx_itens_orcamento_viagem
    ON itens_orcamento(viagem_id);

CREATE INDEX IF NOT EXISTS idx_reservas_viagem
    ON reservas_simuladas(viagem_id);

CREATE INDEX IF NOT EXISTS idx_snapshots_oferta_data
    ON snapshots_preco(tipo_oferta, oferta_id, data_consulta);