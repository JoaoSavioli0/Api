ALTER TABLE public.service_step_contributors
    ADD COLUMN IF NOT EXISTS staff_id   UUID REFERENCES public.staff(id),
    ADD COLUMN IF NOT EXISTS company_id UUID REFERENCES public.company(id);

ALTER TABLE public.service_step_contributors
    ALTER COLUMN name DROP NOT NULL,
    ALTER COLUMN type TYPE VARCHAR(30);
