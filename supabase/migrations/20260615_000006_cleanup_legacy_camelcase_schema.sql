alter table public.professores
  add column if not exists foto_perfil text,
  add column if not exists diploma text,
  add column if not exists status_verificacao text not null default 'INVALIDO',
  add column if not exists valor_hora_aula numeric(12,2);

do $$
begin
  if to_regclass('public."Aluno"') is not null then
    insert into public.pessoas (
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
      a.auth_user_id,
      a.nome,
      a.sobrenome,
      a."dataNascimento"::date,
      coalesce(a.genero::text, 'NAO_INFORMADO')::public.genero_tipo,
      null,
      now(),
      now()
    from public."Aluno" a
    on conflict (auth_user_id) do update
      set nome = excluded.nome,
          sobrenome = excluded.sobrenome,
          data_nascimento = excluded.data_nascimento,
          genero = excluded.genero,
          updated_at = excluded.updated_at;

    insert into public.alunos (
      id,
      pessoa_id,
      created_at,
      updated_at
    )
    select
      gen_random_uuid(),
      p.id,
      now(),
      now()
    from public."Aluno" a
    join public.pessoas p on p.auth_user_id = a.auth_user_id
    left join public.alunos al on al.pessoa_id = p.id
    where al.id is null;
  end if;

  if to_regclass('public."Professor"') is not null then
    insert into public.pessoas (
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
      pr.auth_user_id,
      pr.nome,
      pr.sobrenome,
      pr."dataNascimento",
      coalesce(pr.genero::text, 'NAO_INFORMADO')::public.genero_tipo,
      null,
      now(),
      now()
    from public."Professor" pr
    on conflict (auth_user_id) do update
      set nome = excluded.nome,
          sobrenome = excluded.sobrenome,
          data_nascimento = excluded.data_nascimento,
          genero = excluded.genero,
          updated_at = excluded.updated_at;

    insert into public.professores (
      id,
      pessoa_id,
      cpf,
      bio,
      ativo,
      foto_perfil,
      diploma,
      status_verificacao,
      valor_hora_aula,
      created_at,
      updated_at
    )
    select
      gen_random_uuid(),
      p.id,
      pr.cpf,
      null,
      true,
      pr."fotoPerfil",
      pr.diploma,
      pr."statusVerificacao"::text,
      pr."valorHoraAula",
      now(),
      now()
    from public."Professor" pr
    join public.pessoas p on p.auth_user_id = pr.auth_user_id
    left join public.professores prof on prof.pessoa_id = p.id
    where prof.id is null
    on conflict (pessoa_id) do update
      set cpf = excluded.cpf,
          bio = excluded.bio,
          ativo = excluded.ativo,
          foto_perfil = excluded.foto_perfil,
          diploma = excluded.diploma,
          status_verificacao = excluded.status_verificacao,
          valor_hora_aula = excluded.valor_hora_aula,
          updated_at = excluded.updated_at;
  end if;
end $$;

drop table if exists
  public."professorHasDisciplina",
  public."Avaliacao",
  public."Pagamento",
  public."Aula",
  public."Disponibilidade",
  public."Disciplina",
  public."Categoria",
  public."Professor",
  public."Aluno",
  public."Endereco"
cascade;
