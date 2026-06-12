create or replace function public.handle_new_user_profile()
returns trigger
language plpgsql
security definer
set search_path = public, auth
as $$
declare
  nome text := nullif(trim(coalesce(new.raw_user_meta_data ->> 'nome', '')), '');
  sobrenome text := nullif(trim(coalesce(new.raw_user_meta_data ->> 'sobrenome', '')), '');
  data_nascimento_text text := nullif(trim(coalesce(new.raw_user_meta_data ->> 'data_nascimento', '')), '');
  data_nascimento date := null;
  v_pessoa_id uuid;
begin
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
    coalesce(nome, ''),
    coalesce(sobrenome, ''),
    coalesce(data_nascimento, current_date),
    coalesce(
      nullif(upper(coalesce(new.raw_user_meta_data ->> 'genero', 'NAO_INFORMADO')), '')::public.genero_tipo,
      'NAO_INFORMADO'::public.genero_tipo
    ),
    null,
    nullif(trim(coalesce(new.raw_user_meta_data ->> 'cpf', '')), ''),
    nullif(trim(coalesce(new.raw_user_meta_data ->> 'foto_perfil', '')), '')
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
