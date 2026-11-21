ALTER TABLE viagens
    ADD COLUMN IF NOT EXISTS valor_orcamento_total NUMERIC(12,2);
