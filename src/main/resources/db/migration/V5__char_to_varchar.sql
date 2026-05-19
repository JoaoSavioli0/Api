-- Hibernate valida colunas String como VARCHAR (Types#VARCHAR).
-- CHAR(n) no PostgreSQL é bpchar (Types#CHAR), causando falha no schema
-- validation. Convertemos para VARCHAR sem perda semântica: o CHECK
-- constraint de cpf já garante o comprimento exato via regex.

ALTER TABLE condominium
    ALTER COLUMN cnpj TYPE VARCHAR(14);

ALTER TABLE user_account
    ALTER COLUMN cpf TYPE VARCHAR(11);

ALTER TABLE staff
    ALTER COLUMN company_cnpj TYPE VARCHAR(14);
