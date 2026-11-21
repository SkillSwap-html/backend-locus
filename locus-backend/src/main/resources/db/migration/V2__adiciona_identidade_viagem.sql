ALTER TABLE viagens
    ADD COLUMN IF NOT EXISTS titulo VARCHAR(255);

ALTER TABLE viagens
    ADD COLUMN IF NOT EXISTS tipo_orcamento VARCHAR(255);

UPDATE viagens
SET titulo = CONCAT('Roteiro para ', destino)
WHERE titulo IS NULL OR BTRIM(titulo) = '';

UPDATE viagens
SET tipo_orcamento = 'ABERTO'
WHERE tipo_orcamento IS NULL OR BTRIM(tipo_orcamento) = '';

ALTER TABLE viagens
    ALTER COLUMN titulo SET DEFAULT 'Minha próxima viagem';

ALTER TABLE viagens
    ALTER COLUMN tipo_orcamento SET DEFAULT 'ABERTO';

ALTER TABLE viagens
    ALTER COLUMN titulo SET NOT NULL;

ALTER TABLE viagens
    ALTER COLUMN tipo_orcamento SET NOT NULL;
