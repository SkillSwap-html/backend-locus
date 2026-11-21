UPDATE viagens
SET mochilao = FALSE
WHERE mochilao IS NULL;

ALTER TABLE viagens
    ALTER COLUMN mochilao SET DEFAULT FALSE;

ALTER TABLE viagens
    ALTER COLUMN mochilao SET NOT NULL;
