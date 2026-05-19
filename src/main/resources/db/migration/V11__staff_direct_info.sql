-- V11: Staff passa a armazenar name/phone/job_title diretamente, pois
-- colaboradores sem acesso ao sistema não possuem user_account.
-- Também expande o CHECK de role em condo_member para incluir 'staff'.

ALTER TABLE public.staff
    ADD COLUMN IF NOT EXISTS name      VARCHAR(255),
    ADD COLUMN IF NOT EXISTS phone     VARCHAR(20),
    ADD COLUMN IF NOT EXISTS job_title VARCHAR(100);

-- Remove o CHECK herdado da V6 (foi criado na tabela 'resident' antes do rename)
ALTER TABLE public.condo_member DROP CONSTRAINT IF EXISTS resident_role_check;
ALTER TABLE public.condo_member DROP CONSTRAINT IF EXISTS condo_member_role_check;

ALTER TABLE public.condo_member
    ADD CONSTRAINT condo_member_role_check
    CHECK (role IN ('resident', 'council', 'syndic', 'admin', 'gatekeeper', 'staff'));
