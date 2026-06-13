create or replace function public.handle_new_user_profile()
returns trigger
language plpgsql
security definer
set search_path = public, auth
as $$
declare
  display_name text := nullif(trim(coalesce(
    new.raw_user_meta_data ->> 'full_name',
    new.raw_user_meta_data ->> 'name',
    new.email,
    ''
  )), '');
  nome text := nullif(trim(coalesce(new.raw_user_meta_data ->> 'nome', '')), '');
  sobrenome text := nullif(trim(coalesce(new.raw_user_meta_data ->> 'sobrenome', '')), '');
  data_nascimento_text text := nullif(trim(coalesce(new.raw_user_meta_data ->> 'data_nascimento', '')), '');
  data_nascimento date := null;
  foto_perfil text := nullif(trim(coalesce(
    new.raw_user_meta_data ->> 'foto_perfil',
    new.raw_user_meta_data ->> 'avatar_url',
    new.raw_user_meta_data ->> 'picture',
    ''
  )), '');
  v_pessoa_id uuid;
begin
  if nome is null and display_name is not null then
    nome := split_part(display_name, ' ', 1);
  end if;

  if sobrenome is null and display_name is not null then
    sobrenome := nullif(trim(regexp_replace(display_name, '^\S+\s*', '')), '');
  end if;

  if data_nascimento_text is not null then
    data_nascimento := data_nascimento_text::date;
  end if;

  insert into public.pessoas (
    auth_user_id,
    nome,
    sobrenome,
    data_nascimento,
    genero,
    endereco_id,
    cpf,
    foto_perfil
  )
  values (
    new.id,
    coalesce(nome, 'Nao informado'),
    coalesce(sobrenome, 'Nao informado'),
    coalesce(data_nascimento, current_date),
    coalesce(
      nullif(upper(coalesce(new.raw_user_meta_data ->> 'genero', 'NAO_INFORMADO')), '')::public.genero_tipo,
      'NAO_INFORMADO'::public.genero_tipo
    ),
    null,
    nullif(trim(coalesce(new.raw_user_meta_data ->> 'cpf', '')), ''),
    foto_perfil
  )
  on conflict (auth_user_id) do update
    set nome = excluded.nome,
        sobrenome = excluded.sobrenome,
        data_nascimento = excluded.data_nascimento,
        genero = excluded.genero,
        cpf = excluded.cpf,
        foto_perfil = excluded.foto_perfil,
        updated_at = now()
  returning id into v_pessoa_id;

  insert into public.alunos (
    pessoa_id
  )
  values (
    v_pessoa_id
  )
  on conflict (pessoa_id) do update
    set updated_at = now();

  insert into public.professores (
    pessoa_id,
    bio,
    ativo
  )
  values (
    v_pessoa_id,
    null,
    true
  )
  on conflict (pessoa_id) do update
    set updated_at = now();

  return new;
end;
$$;
