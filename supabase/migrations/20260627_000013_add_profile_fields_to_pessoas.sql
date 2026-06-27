alter table public.pessoas
  add column if not exists cpf text,
  add column if not exists foto_perfil text;
