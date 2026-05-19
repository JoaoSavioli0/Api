CREATE TABLE unit (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    condominium_id UUID NOT NULL REFERENCES condominium(id) ON DELETE CASCADE,
    identifier    VARCHAR(50) NOT NULL,
    block         VARCHAR(50),
    street        VARCHAR(100),
    floor         INTEGER,
    type          TEXT NOT NULL CHECK (type IN ('apartment', 'house')),
    UNIQUE (condominium_id, type, identifier, block)
);

ALTER TABLE condo_member
    ADD COLUMN unit_id UUID REFERENCES unit(id);

ALTER TABLE condo_member
    DROP COLUMN unit_address;
