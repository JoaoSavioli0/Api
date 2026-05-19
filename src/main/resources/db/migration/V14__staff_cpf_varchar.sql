-- V13 criou cpf como CHAR(11) mas Hibernate mapeia String para VARCHAR.
-- Converte para VARCHAR para alinhar com o mapeamento JPA.
ALTER TABLE public.staff ALTER COLUMN cpf TYPE VARCHAR(11);
