create or replace function public.handle_new_user_profile()
returns trigger
language plpgsql
security definer
set search_path = public, auth
as $$
declare
  profile_type text := lower(coalesce(new.raw_user_meta_data ->> 'profile_type', 'aluno'));
  nome text := nullif(trim(coalesce(new.raw_user_meta_data ->> 'nome', '')), '');
  sobrenome text := nullif(trim(coalesce(new.raw_user_meta_data ->> 'sobrenome', '')), '');
  cpf_numeros text := regexp_replace(coalesce(new.raw_user_meta_data ->> 'cpf', ''), '\D', '', 'g');
  data_nascimento_text text := nullif(trim(coalesce(new.raw_user_meta_data ->> 'data_nascimento', '')), '');
  data_nascimento date := null;
  pessoa_id uuid;
begin
  if data_nascimento_text is not null then
    data_nascimento := data_nascimento_text::date;
  end if;

  if profile_type in ('professor', 'ambos', 'both') then
    if data_nascimento is null then
      raise exception 'Data de nascimento obrigatoria para professor.';
    end if;

    if date_part('year', age(current_date, data_nascimento)) < 18 then
      raise exception 'Professor menor de idade nao pode ministrar aulas.';
    end if;

  end if;

  insert into public.pessoas (
    auth_user_id,
    nome,
    sobrenome,
    data_nascimento,
    genero,
    endereco_id
  )
  values (
    new.id,
    coalesce(nome, ''),
    coalesce(sobrenome, ''),
    coalesce(data_nascimento, current_date),
    coalesce(
      nullif(upper(coalesce(new.raw_user_meta_data ->> 'genero', 'NAO_INFORMADO')), '')::public.genero_tipo,
      'NAO_INFORMADO'::public.genero_tipo
    ),
    null
  )
  on conflict (auth_user_id) do update
    set nome = excluded.nome,
        sobrenome = excluded.sobrenome,
        data_nascimento = excluded.data_nascimento,
        genero = excluded.genero,
        updated_at = now()
  returning id into pessoa_id;

  if profile_type in ('aluno', 'ambos', 'both') then
    insert into public.alunos (
      pessoa_id
    )
    values (
      pessoa_id
    )
    on conflict (pessoa_id) do update
      set updated_at = now();
  end if;

  if profile_type in ('professor', 'ambos', 'both') then
    insert into public.professores (
      pessoa_id,
      cpf,
      bio,
      ativo
    )
    values (
      pessoa_id,
      cpf_numeros,
      null,
      true
    )
    on conflict (pessoa_id) do update
      set cpf = excluded.cpf,
          bio = excluded.bio,
          ativo = excluded.ativo,
          updated_at = now();
  end if;

  return new;
end;
$$;
