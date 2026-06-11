create table if not exists public.pessoas (
  id uuid primary key default gen_random_uuid(),
  auth_user_id uuid unique references auth.users (id) on delete set null,
  nome text not null,
  sobrenome text not null,
  data_nascimento date not null,
  genero public.genero_tipo not null default 'NAO_INFORMADO',
  endereco_id uuid references public.enderecos (id) on delete set null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

alter table public.alunos
  add column if not exists pessoa_id uuid;

alter table public.professores
  add column if not exists pessoa_id uuid;

insert into public.pessoas (
  id,
  auth_user_id,
  nome,
  sobrenome,
  data_nascimento,
  genero,
  endereco_id,
  created_at,
  updated_at
)
select
  gen_random_uuid(),
  coalesce(a.auth_user_id, p.auth_user_id),
  coalesce(nullif(trim(a.nome), ''), nullif(trim(p.nome), ''), 'Não informado'),
  coalesce(nullif(trim(a.sobrenome), ''), nullif(trim(p.sobrenome), ''), 'Não informado'),
  coalesce(a.data_nascimento, p.data_nascimento, current_date),
  coalesce(a.genero, 'NAO_INFORMADO'::public.genero_tipo),
  coalesce(a.endereco_id, p.endereco_id),
  greatest(coalesce(a.created_at, now()), coalesce(p.created_at, now())),
  greatest(coalesce(a.updated_at, now()), coalesce(p.updated_at, now()))
from public.alunos a
full outer join public.professores p on p.auth_user_id = a.auth_user_id
where coalesce(a.auth_user_id, p.auth_user_id) is not null
on conflict (auth_user_id) do update
  set nome = excluded.nome,
      sobrenome = excluded.sobrenome,
      data_nascimento = excluded.data_nascimento,
      genero = excluded.genero,
      endereco_id = excluded.endereco_id,
      updated_at = excluded.updated_at;

update public.alunos a
set pessoa_id = p.id
from public.pessoas p
where a.auth_user_id = p.auth_user_id;

update public.professores pr
set pessoa_id = p.id
from public.pessoas p
where pr.auth_user_id = p.auth_user_id;

alter table public.alunos
  alter column pessoa_id set not null;

alter table public.professores
  alter column pessoa_id set not null;

create unique index if not exists idx_alunos_pessoa_id on public.alunos (pessoa_id);
create unique index if not exists idx_professores_pessoa_id on public.professores (pessoa_id);

do $$
begin
  if not exists (
    select 1
    from information_schema.table_constraints
    where table_schema = 'public'
      and table_name = 'alunos'
      and constraint_name = 'alunos_pessoa_id_fkey'
  ) then
    alter table public.alunos
      add constraint alunos_pessoa_id_fkey
      foreign key (pessoa_id)
      references public.pessoas (id)
      on delete cascade;
  end if;
end $$;

do $$
begin
  if not exists (
    select 1
    from information_schema.table_constraints
    where table_schema = 'public'
      and table_name = 'professores'
      and constraint_name = 'professores_pessoa_id_fkey'
  ) then
    alter table public.professores
      add constraint professores_pessoa_id_fkey
      foreign key (pessoa_id)
      references public.pessoas (id)
      on delete cascade;
  end if;
end $$;

alter table public.alunos
  drop column if exists auth_user_id,
  drop column if exists nome,
  drop column if exists sobrenome,
  drop column if exists data_nascimento,
  drop column if exists genero,
  drop column if exists endereco_id;

alter table public.professores
  drop column if exists auth_user_id,
  drop column if exists nome,
  drop column if exists sobrenome,
  drop column if exists data_nascimento,
  drop column if exists endereco_id;
