create extension if not exists "pgcrypto";

do $$
begin
  create type public.genero_tipo as enum ('feminino', 'masculino', 'outro', 'nao_informado');
exception
  when duplicate_object then null;
end $$;

do $$
begin
  create type public.aula_status as enum ('agendada', 'confirmada', 'concluida', 'cancelada', 'reagendada');
exception
  when duplicate_object then null;
end $$;

do $$
begin
  create type public.disponibilidade_status as enum ('disponivel', 'reservada', 'bloqueada');
exception
  when duplicate_object then null;
end $$;

do $$
begin
  create type public.pagamento_status as enum ('pendente', 'aprovado', 'recusado', 'reembolsado', 'cancelado');
exception
  when duplicate_object then null;
end $$;

create table if not exists public.enderecos (
  id uuid primary key default gen_random_uuid(),
  rua text not null,
  numero text not null,
  complemento text,
  bairro text not null,
  cidade text not null,
  estado text not null,
  cep text not null,
  pais text not null default 'Brasil',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.alunos (
  id uuid primary key default gen_random_uuid(),
  auth_user_id uuid unique references auth.users (id) on delete set null,
  nome text not null,
  sobrenome text not null,
  data_nascimento date not null,
  genero public.genero_tipo not null default 'nao_informado',
  endereco_id uuid references public.enderecos (id) on delete set null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.professores (
  id uuid primary key default gen_random_uuid(),
  auth_user_id uuid unique references auth.users (id) on delete set null,
  nome text not null,
  sobrenome text not null,
  bio text,
  endereco_id uuid references public.enderecos (id) on delete set null,
  ativo boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.disciplinas (
  id uuid primary key default gen_random_uuid(),
  nome text not null unique,
  descricao text,
  ativo boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.disponibilidades (
  id uuid primary key default gen_random_uuid(),
  professor_id uuid not null references public.professores (id) on delete cascade,
  inicio timestamptz not null,
  fim timestamptz not null,
  status public.disponibilidade_status not null default 'disponivel',
  observacao text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint disponibilidades_periodo_check check (fim > inicio)
);

create table if not exists public.aulas (
  id uuid primary key default gen_random_uuid(),
  aluno_id uuid not null references public.alunos (id) on delete cascade,
  professor_id uuid not null references public.professores (id) on delete cascade,
  disciplina_id uuid not null references public.disciplinas (id) on delete restrict,
  inicio timestamptz not null,
  fim timestamptz not null,
  status public.aula_status not null default 'agendada',
  modalidade text not null default 'online',
  observacao text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint aulas_periodo_check check (fim > inicio)
);

create table if not exists public.pagamentos (
  id uuid primary key default gen_random_uuid(),
  aula_id uuid references public.aulas (id) on delete set null,
  aluno_id uuid not null references public.alunos (id) on delete cascade,
  valor numeric(12,2) not null,
  status public.pagamento_status not null default 'pendente',
  data_vencimento date,
  data_pagamento timestamptz,
  metodo_pagamento text,
  referencia_externa text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint pagamentos_valor_check check (valor >= 0)
);

create table if not exists public.avaliacoes (
  id uuid primary key default gen_random_uuid(),
  aula_id uuid not null references public.aulas (id) on delete cascade,
  aluno_id uuid not null references public.alunos (id) on delete cascade,
  professor_id uuid not null references public.professores (id) on delete cascade,
  nota smallint not null,
  comentario text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint avaliacoes_nota_check check (nota between 1 and 5)
);

create table if not exists public.penalidades (
  id uuid primary key default gen_random_uuid(),
  aluno_id uuid not null references public.alunos (id) on delete cascade,
  aula_id uuid references public.aulas (id) on delete set null,
  motivo text not null,
  valor numeric(12,2),
  aplicada_em timestamptz not null default now(),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint penalidades_valor_check check (valor is null or valor >= 0)
);

create index if not exists idx_alunos_endereco_id on public.alunos (endereco_id);
create index if not exists idx_professores_endereco_id on public.professores (endereco_id);
create index if not exists idx_disponibilidades_professor_id on public.disponibilidades (professor_id);
create index if not exists idx_aulas_aluno_id on public.aulas (aluno_id);
create index if not exists idx_aulas_professor_id on public.aulas (professor_id);
create index if not exists idx_aulas_disciplina_id on public.aulas (disciplina_id);
create index if not exists idx_pagamentos_aluno_id on public.pagamentos (aluno_id);
create index if not exists idx_pagamentos_aula_id on public.pagamentos (aula_id);
create index if not exists idx_avaliacoes_aula_id on public.avaliacoes (aula_id);
create index if not exists idx_penalidades_aluno_id on public.penalidades (aluno_id);
