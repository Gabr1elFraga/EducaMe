create unique index if not exists idx_alunos_pessoa_id on public.alunos (pessoa_id);
create unique index if not exists idx_professores_pessoa_id on public.professores (pessoa_id);

do $$
begin
  if not exists (
    select 1
    from pg_constraint
    where conname = 'alunos_pessoa_id_unique'
  ) then
    alter table public.alunos
      add constraint alunos_pessoa_id_unique unique using index idx_alunos_pessoa_id;
  end if;
end $$;

do $$
begin
  if not exists (
    select 1
    from pg_constraint
    where conname = 'professores_pessoa_id_unique'
  ) then
    alter table public.professores
      add constraint professores_pessoa_id_unique unique using index idx_professores_pessoa_id;
  end if;
end $$;
