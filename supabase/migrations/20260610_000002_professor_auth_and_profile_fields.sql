alter table public.alunos
  add column if not exists auth_user_id uuid;

alter table public.professores
  add column if not exists auth_user_id uuid;

alter table public.professores
  add column if not exists cpf text;

alter table public.professores
  add column if not exists data_nascimento date;

create unique index if not exists idx_alunos_auth_user_id on public.alunos (auth_user_id);
create unique index if not exists idx_professores_auth_user_id on public.professores (auth_user_id);
create unique index if not exists idx_professores_cpf on public.professores (cpf);

do $$
begin
  if not exists (
    select 1
    from information_schema.table_constraints
    where table_schema = 'public'
      and table_name = 'alunos'
      and constraint_name = 'alunos_auth_user_id_fkey'
  ) then
    alter table public.alunos
      add constraint alunos_auth_user_id_fkey
      foreign key (auth_user_id)
      references auth.users (id)
      on delete set null;
  end if;
end $$;

do $$
begin
  if not exists (
    select 1
    from information_schema.table_constraints
    where table_schema = 'public'
      and table_name = 'professores'
      and constraint_name = 'professores_auth_user_id_fkey'
  ) then
    alter table public.professores
      add constraint professores_auth_user_id_fkey
      foreign key (auth_user_id)
      references auth.users (id)
      on delete set null;
  end if;
end $$;
