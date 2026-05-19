ALTER TABLE condominium ADD COLUMN IF NOT EXISTS credits INT NOT NULL DEFAULT 0;

CREATE TABLE insight_generations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    condominium_id  UUID NOT NULL REFERENCES condominium(id),
    generated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    credits_used    INT NOT NULL,
    period_start    DATE NOT NULL,
    period_end      DATE NOT NULL,
    analysis_depth  VARCHAR(20) NOT NULL DEFAULT 'basic',
    data_sources    VARCHAR(255) NOT NULL DEFAULT '',
    status          VARCHAR(20) NOT NULL DEFAULT 'completed',
    error_message   TEXT
);

CREATE TABLE insights (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    generation_id UUID NOT NULL REFERENCES insight_generations(id) ON DELETE CASCADE,
    condominium_id UUID NOT NULL,
    category      VARCHAR(50) NOT NULL,
    severity      VARCHAR(20) NOT NULL,
    title         VARCHAR(255) NOT NULL,
    description   TEXT NOT NULL,
    action_label  VARCHAR(100),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_insight_generations_condo ON insight_generations(condominium_id, generated_at DESC);
CREATE INDEX idx_insights_generation ON insights(generation_id);
CREATE INDEX idx_insights_condo ON insights(condominium_id);
