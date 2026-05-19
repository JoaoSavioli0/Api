CREATE TABLE ticket_image (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id   UUID        NOT NULL REFERENCES ticket(id) ON DELETE CASCADE,
    url         TEXT        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE ticket_image ENABLE ROW LEVEL SECURITY;

CREATE POLICY ticket_image_isolation ON ticket_image
    USING (
        EXISTS (
            SELECT 1 FROM ticket
            JOIN post ON post.id = ticket.id
            WHERE ticket.id = ticket_image.ticket_id
              AND post.condominium_id = current_condominium_id()
        )
    );

CREATE INDEX idx_ticket_image_ticket ON ticket_image(ticket_id);
