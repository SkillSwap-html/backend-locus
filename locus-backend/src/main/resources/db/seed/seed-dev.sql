-- Dados opcionais para desenvolvimento local.
-- Execute somente em um banco de desenvolvimento.

INSERT INTO usuarios (firebase_uid, nome, email, termos_aceitos, tipo_usuario)
SELECT 'dev-locus-uid', 'Usuário de Desenvolvimento', 'dev@locus.local', TRUE, 'ADMINISTRADOR'
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE email = 'dev@locus.local'
);
