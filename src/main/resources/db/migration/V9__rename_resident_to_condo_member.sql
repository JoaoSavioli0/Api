ALTER TABLE public.resident RENAME TO condo_member;

ALTER TABLE public.booking       RENAME COLUMN resident_id TO member_id;
ALTER TABLE public.post          RENAME COLUMN resident_id TO member_id;
ALTER TABLE public.comment       RENAME COLUMN resident_id TO member_id;
ALTER TABLE public.like_         RENAME COLUMN resident_id TO member_id;
ALTER TABLE public.notification  RENAME COLUMN resident_id TO member_id;
ALTER TABLE public.visitor       RENAME COLUMN resident_id TO member_id;
ALTER TABLE public.poll_vote     RENAME COLUMN resident_id TO member_id;

CREATE OR REPLACE FUNCTION public.fn_check_single_vote() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM poll_vote pv
        JOIN poll_option po ON po.id = pv.option_id
        WHERE po.poll_id = (SELECT poll_id FROM poll_option WHERE id = NEW.option_id)
          AND pv.member_id = NEW.member_id
    ) THEN
        RAISE EXCEPTION 'Member has already voted in this poll.';
    END IF;
    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION public.fn_notify_visitor_arrived() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.status = 'arrived' AND OLD.status <> 'arrived' THEN
        INSERT INTO notification (condominium_id, member_id, type, title, body, reference_id, reference_table)
        VALUES (
            NEW.condominium_id,
            NEW.member_id,
            'visitor_arrived',
            'Your visitor has arrived',
            NEW.name || ' is at the gatehouse.',
            NEW.id,
            'visitor'
        );
    END IF;
    RETURN NEW;
END;
$$;
