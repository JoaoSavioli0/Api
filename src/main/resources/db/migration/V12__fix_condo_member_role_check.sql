-- V12: Remove o CHECK constraint com nome incorreto ("delivery_status_check")
-- que foi criado na tabela condo_member em uma migração anterior.
-- O constraint correto ("condo_member_role_check") já foi adicionado no V11.

ALTER TABLE public.condo_member DROP CONSTRAINT IF EXISTS delivery_status_check;
