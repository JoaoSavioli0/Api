CREATE TABLE service_lines (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    condominium_id     UUID         NOT NULL REFERENCES condominium(id) ON DELETE CASCADE,
    title              VARCHAR(255) NOT NULL,
    description        TEXT         NOT NULL,
    status             VARCHAR(20)  NOT NULL DEFAULT 'planejado',
    responsible_name   VARCHAR(255) NOT NULL,
    start_date         DATE         NOT NULL,
    estimated_end_date VARCHAR(10),
    estimated_cost     VARCHAR(100),
    linked_request_id  UUID,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE service_steps (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    service_line_id UUID         NOT NULL REFERENCES service_lines(id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    start_date      DATE         NOT NULL,
    end_date        DATE,
    is_public       BOOLEAN      NOT NULL DEFAULT true,
    note            TEXT,
    order_index     INTEGER      NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE service_step_contributors (
    id      UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    step_id UUID         NOT NULL REFERENCES service_steps(id) ON DELETE CASCADE,
    name    VARCHAR(255) NOT NULL,
    type    VARCHAR(20)  NOT NULL,
    role    VARCHAR(255)
);

CREATE TABLE service_step_attachments (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    step_id    UUID         NOT NULL REFERENCES service_steps(id) ON DELETE CASCADE,
    name       VARCHAR(255) NOT NULL,
    type       VARCHAR(20)  NOT NULL,
    url        TEXT         NOT NULL,
    file_size  INTEGER,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);
