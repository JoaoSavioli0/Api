-- V4: Introduz user_account como identidade global da pessoa.
-- resident e staff deixam de ser entidades independentes e passam
-- a referenciar user_account via user_id; condominium_id permanece
-- nas subtabelas porque cada registro representa a relação entre
-- uma pessoa e um condomínio específico.
-- Uma pessoa pode ser morador em vários condomínios: cada um gera
-- uma linha em resident, todas apontando para o mesmo user_account.

-- ============================================================
-- 1. Identidade global (sem condomínio)
-- ============================================================

CREATE TABLE user_account (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name            TEXT        NOT NULL,
    email           CITEXT      NOT NULL,
    cpf             CHAR(11),
    rg              VARCHAR(20),
    phone           VARCHAR(20),
    avatar_url      TEXT,
    password_hash   TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (email),
    UNIQUE (cpf),
    CONSTRAINT chk_user_cpf CHECK (cpf IS NULL OR cpf ~ '^\d{11}$')
);

-- user_account é global; RLS fica nas tabelas de membship (resident/staff)

-- ============================================================
-- 2. Migração de dados existentes
-- ============================================================

-- Moradores → user_account (DISTINCT ON email para caso de moradores
-- em múltiplos condomínios — pega o registro mais antigo)
INSERT INTO user_account (name, email, cpf, rg, phone, avatar_url, password_hash)
SELECT DISTINCT ON (email) name, email, cpf, rg, phone, avatar_url, password_hash
FROM resident
ORDER BY email, joined_at;

-- Funcionários sem e-mail correspondente em user_account
INSERT INTO user_account (name, email, cpf, rg, phone)
SELECT name, email, cpf, rg, phone
FROM staff
WHERE email IS NOT NULL
ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- 3. Transforma resident: adiciona user_id, remove campos movidos
-- ============================================================

ALTER TABLE resident ADD COLUMN user_id UUID;

UPDATE resident r
SET user_id = ua.id
FROM user_account ua
WHERE ua.email = r.email;

ALTER TABLE resident ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE resident
    ADD CONSTRAINT resident_user_fk
        FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
    ADD CONSTRAINT resident_user_condominium_unique
        UNIQUE (user_id, condominium_id);

-- CASCADE remove as unique constraints (condominium_id, email) e
-- (condominium_id, cpf) que dependem dessas colunas
ALTER TABLE resident
    DROP COLUMN name CASCADE,
    DROP COLUMN email CASCADE,
    DROP COLUMN cpf CASCADE,
    DROP COLUMN rg,
    DROP COLUMN phone,
    DROP COLUMN avatar_url,
    DROP COLUMN password_hash;

-- RLS de resident não muda: condominium_id permanece na tabela

-- ============================================================
-- 4. Transforma staff: adiciona user_id, remove campos movidos
-- ============================================================

ALTER TABLE staff ADD COLUMN user_id UUID;

UPDATE staff s
SET user_id = ua.id
FROM user_account ua
WHERE ua.email = s.email;

-- user_id nullable em staff: funcionários externos podem não ter conta
ALTER TABLE staff
    ADD CONSTRAINT staff_user_fk
        FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE SET NULL,
    ADD CONSTRAINT staff_user_condominium_unique
        UNIQUE (user_id, condominium_id);

ALTER TABLE staff
    DROP COLUMN name CASCADE,
    DROP COLUMN email CASCADE,
    DROP COLUMN cpf CASCADE,
    DROP COLUMN rg,
    DROP COLUMN phone;

-- RLS de staff não muda: condominium_id permanece na tabela

-- ============================================================
-- 5. Índices
-- ============================================================

CREATE INDEX idx_user_account_email ON user_account(email);
CREATE INDEX idx_resident_user       ON resident(user_id);
CREATE INDEX idx_staff_user          ON staff(user_id);
