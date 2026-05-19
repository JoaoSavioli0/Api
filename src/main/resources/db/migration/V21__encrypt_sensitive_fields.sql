-- V21: Expande colunas de dados sensíveis para acomodar valores
-- criptografados com AES-256-GCM (IV 12 bytes + tag 16 bytes → ~300 chars em Base64).
-- Remove constraints de formato que não se aplicam a dados criptografados.
-- ATENÇÃO: dados existentes em texto claro são removidos; realize backup antes.

ALTER TABLE user_account ALTER COLUMN cpf   TYPE VARCHAR(300);
ALTER TABLE user_account ALTER COLUMN rg    TYPE VARCHAR(300);
ALTER TABLE user_account ALTER COLUMN phone TYPE VARCHAR(300);

ALTER TABLE user_account DROP CONSTRAINT IF EXISTS chk_user_cpf;

-- Limpa dados antigos em texto claro; novos registros serão criptografados pela aplicação
UPDATE user_account SET cpf = NULL, rg = NULL, phone = NULL;
