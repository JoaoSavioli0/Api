-- V22: Tabela de tokens revogados (blacklist de logout).
-- TTL implícito: tokens são removidos após expires_at via job agendado na aplicação.

CREATE TABLE revoked_token (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    jti         UUID        NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    revoked_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_revoked_token_jti ON revoked_token(jti);
