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
begin
  if data_nascimento_text is not null then
    data_nascimento := data_nascimento_text::date;
  end if;

  if profile_type = 'professor' then
    if data_nascimento is null then
      raise exception 'Data de nascimento obrigatoria para professor.';
    end if;

    if date_part('year', age(current_date, data_nascimento)) < 18 then
      raise exception 'Professor menor de idade nao pode ministrar aulas.';
    end if;

    insert into public.professores (
      auth_user_id,
      nome,
      sobrenome,
      cpf,
      data_nascimento,
      bio,
      ativo
    )
    values (
      new.id,
      coalesce(nome, ''),
      coalesce(sobrenome, ''),
      cpf_numeros,
      data_nascimento,
      null,
      true
    )
    on conflict (auth_user_id) do update
      set nome = excluded.nome,
          sobrenome = excluded.sobrenome,
          cpf = excluded.cpf,
          data_nascimento = excluded.data_nascimento,
          bio = excluded.bio,
          ativo = excluded.ativo;
  else
    insert into public.alunos (
      auth_user_id,
      nome,
      sobrenome,
      data_nascimento,
      genero
    )
    values (
      new.id,
      coalesce(nome, ''),
      coalesce(sobrenome, ''),
      data_nascimento,
      'NAO_INFORMADO'
    )
    on conflict (auth_user_id) do update
      set nome = excluded.nome,
          sobrenome = excluded.sobrenome,
          data_nascimento = excluded.data_nascimento,
          genero = excluded.genero;
  end if;

  return new;
end;
$$;

do $$
begin
  if not exists (
    select 1
    from pg_trigger
    where tgname = 'on_auth_user_created'
  ) then
    create trigger on_auth_user_created
      after insert on auth.users
      for each row
      execute function public.handle_new_user_profile();
  end if;
end $$;
