ALTER TABLE notice
    ADD COLUMN IF NOT EXISTS target_type  VARCHAR(20) NOT NULL DEFAULT 'all',
    ADD COLUMN IF NOT EXISTS target_value VARCHAR(100);

CREATE TABLE notice_reads (
    id        UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    notice_id UUID        NOT NULL REFERENCES notice(id) ON DELETE CASCADE,
    member_id UUID        NOT NULL REFERENCES condo_member(id) ON DELETE CASCADE,
    read_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (notice_id, member_id)
);

CREATE INDEX idx_notice_reads_notice ON notice_reads(notice_id);
CREATE INDEX idx_notice_reads_member ON notice_reads(member_id);
