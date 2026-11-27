-- Bases antigas do Railway podem ter sido marcadas como baseline antes das
-- migrations de mochilão/orçamento. Esta migration é idempotente e só cria
-- colunas ausentes; não altera nem remove dados existentes.
ALTER TABLE viagens
    ADD COLUMN IF NOT EXISTS mochilao BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE viagens
    ADD COLUMN IF NOT EXISTS valor_orcamento_total NUMERIC(12,2);

UPDATE viagens
SET mochilao = FALSE
WHERE mochilao IS NULL;

ALTER TABLE viagens
    ALTER COLUMN mochilao SET DEFAULT FALSE;

ALTER TABLE viagens
    ALTER COLUMN mochilao SET NOT NULL;
