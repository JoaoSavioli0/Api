ALTER TABLE resident
    ADD COLUMN role VARCHAR(10) NOT NULL DEFAULT 'resident'
        CHECK (role IN ('resident', 'council', 'syndic'));
