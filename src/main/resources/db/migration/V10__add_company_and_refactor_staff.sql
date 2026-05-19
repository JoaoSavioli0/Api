CREATE TABLE public.company (
    id             uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    condominium_id uuid        NOT NULL REFERENCES public.condominium(id),
    name           text        NOT NULL,
    cnpj           character varying(14),
    phone          character varying(20),
    email          text,
    active         boolean     DEFAULT true NOT NULL,
    created_at     timestamp with time zone DEFAULT now() NOT NULL
);

ALTER TABLE public.staff ADD COLUMN company_id uuid REFERENCES public.company(id);
ALTER TABLE public.staff DROP COLUMN company_name;
ALTER TABLE public.staff DROP COLUMN company_cnpj;
