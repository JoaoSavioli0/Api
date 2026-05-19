ALTER TABLE comment
ADD COLUMN deleted_at TIMESTAMPTZ,
ADD COLUMN deleted_by UUID;

CREATE INDEX idx_comment_not_deleted
ON comment (deleted_at)
WHERE deleted_at IS NULL;