create table if not exists public.anuncios_aula (
  id uuid primary key default gen_random_uuid(),
  professor_id uuid not null references public.professores (id) on delete cascade,
  disciplina_id uuid not null references public.disciplinas (id) on delete restrict,
  titulo text not null,
  descricao text,
  valor_hora numeric(12,2) not null,
  modalidade text not null default 'online',
  ativo boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint anuncios_aula_valor_hora_check check (valor_hora >= 0)
);

alter table public.disponibilidades
  add column if not exists anuncio_id uuid references public.anuncios_aula (id) on delete cascade;

alter table public.aulas
  add column if not exists anuncio_id uuid references public.anuncios_aula (id) on delete set null,
  add column if not exists disponibilidade_id uuid references public.disponibilidades (id) on delete set null,
  add column if not exists valor_aula numeric(12,2);

create index if not exists idx_anuncios_aula_professor_id on public.anuncios_aula (professor_id);
create index if not exists idx_anuncios_aula_disciplina_id on public.anuncios_aula (disciplina_id);
create index if not exists idx_anuncios_aula_ativo on public.anuncios_aula (ativo);
create index if not exists idx_disponibilidades_anuncio_id on public.disponibilidades (anuncio_id);
create index if not exists idx_aulas_anuncio_id on public.aulas (anuncio_id);

create unique index if not exists idx_aulas_disponibilidade_id_unique
  on public.aulas (disponibilidade_id)
  where disponibilidade_id is not null;
