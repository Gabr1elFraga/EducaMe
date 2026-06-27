package com.educame.educame_api.infrastructure.persistence.supabase;

import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.contract.ProfileCadastroRepository;
import com.educame.educame_api.domain.enums.GeneroTipo;
import com.educame.educame_api.domain.endereco.Endereco;
import com.educame.educame_api.domain.professor.Professor;
import com.educame.educame_api.infrastructure.persistence.jdbc.PessoaJdbcRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("supabase")
@ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText('${SUPABASE_DB_URL:}')")
@Primary
public class SupabaseProfileCadastroRepository implements ProfileCadastroRepository {
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final PessoaJdbcRepository pessoaJdbcRepository;

	public SupabaseProfileCadastroRepository(
		NamedParameterJdbcTemplate jdbcTemplate,
		PessoaJdbcRepository pessoaJdbcRepository
	) {
		this.jdbcTemplate = jdbcTemplate;
		this.pessoaJdbcRepository = pessoaJdbcRepository;
	}

	@Override
	public Optional<Aluno> findAlunoByAuthUserId(UUID authUserId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select a.id,
					   p.auth_user_id,
					   p.nome,
					   p.sobrenome,
					   p.data_nascimento,
					   p.genero,
					   p.endereco_id
				from public.alunos a
				join public.pessoas p on p.id = a.pessoa_id
				where p.auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), alunoRowMapper()));
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Professor> findProfessorByAuthUserId(UUID authUserId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select pr.id,
					   p.auth_user_id,
					   p.nome,
					   p.sobrenome,
					   null as cpf,
					   p.data_nascimento,
					   pr.bio,
					   pr.ativo,
					   pr.diploma,
					   pr.status_verificacao,
					   pr.valor_hora_aula,
					   p.endereco_id
				from public.professores pr
				join public.pessoas p on p.id = pr.pessoa_id
				where p.auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), professorRowMapper()));
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Professor ensureProfessorByAuthUserId(UUID authUserId, Professor professorFallback) {
		var existingProfessor = findProfessorByAuthUserId(authUserId);
		if (existingProfessor.isPresent()) {
			return existingProfessor.get();
		}

		var pessoaId = pessoaJdbcRepository.findIdByAuthUserId(authUserId).orElseGet(() -> {
			if (professorFallback == null || professorFallback.getPessoa() == null) {
				throw new IllegalArgumentException("Pessoa nao encontrada.");
			}

			var fallbackPessoa = professorFallback.getPessoa();
			return pessoaJdbcRepository.ensureMinimalPessoa(
				authUserId,
				fallbackPessoa.getNome(),
				fallbackPessoa.getSobrenome(),
				fallbackPessoa.getDataNascimento(),
				fallbackPessoa.getGenero()
			);
		});

		var professorId = jdbcTemplate.queryForObject("""
			insert into public.professores (
				id,
				pessoa_id,
				bio,
				ativo,
				diploma,
				status_verificacao,
				valor_hora_aula,
				created_at,
				updated_at
			)
			values (:id, :pessoaId, null, true, null, 'PENDENTE', null, now(), now())
			on conflict (pessoa_id) do update
				set updated_at = excluded.updated_at
			returning id
			""", new MapSqlParameterSource()
			.addValue("id", UUID.randomUUID())
			.addValue("pessoaId", pessoaId), UUID.class);

		return findProfessorById(professorId).orElseThrow();
	}

	@Override
	public Aluno saveAluno(Aluno aluno) {
		var id = aluno.getId() != null ? aluno.getId() : UUID.randomUUID();
		var existingProfile = findAlunoProfileByAuthUserId(aluno.getAuthUserId());
		var pessoaId = pessoaJdbcRepository.upsertPessoa(
			existingProfile.map(ProfileRow::pessoaId).orElse(null),
			aluno.getAuthUserId(),
			aluno.getNome(),
			aluno.getSobrenome(),
			aluno.getDataNascimento(),
			aluno.getGenero() != null ? aluno.getGenero() : GeneroTipo.NAO_INFORMADO,
			aluno.getEndereco() != null ? aluno.getEndereco().getId() : null,
			null,
			null
		);

		if (existingProfile.isPresent()) {
			jdbcTemplate.update("""
				update public.alunos
				set pessoa_id = :pessoaId,
					updated_at = now()
				where id = :id
				""", new MapSqlParameterSource()
				.addValue("id", existingProfile.get().id())
				.addValue("pessoaId", pessoaId));
		} else {
			jdbcTemplate.update("""
				insert into public.alunos (id, pessoa_id, created_at, updated_at)
				values (:id, :pessoaId, now(), now())
				""", new MapSqlParameterSource()
				.addValue("id", id)
				.addValue("pessoaId", pessoaId));
		}

		return findAlunoByAuthUserId(aluno.getAuthUserId()).orElseThrow();
	}

	@Override
	public Professor saveProfessor(Professor professor) {
		var id = professor.getId() != null ? professor.getId() : UUID.randomUUID();
		var existingProfile = findProfessorProfileByAuthUserId(professor.getAuthUserId());
		var pessoaId = pessoaJdbcRepository.upsertPessoa(
			existingProfile.map(ProfileRow::pessoaId).orElse(null),
			professor.getAuthUserId(),
			professor.getNome(),
			professor.getSobrenome(),
			professor.getDataNascimento(),
			professor.getPessoa() != null && professor.getPessoa().getGenero() != null
				? professor.getPessoa().getGenero()
				: GeneroTipo.NAO_INFORMADO,
			professor.getEndereco() != null ? professor.getEndereco().getId() : null,
			professor.getCpf(),
			professor.getPessoa() != null ? professor.getPessoa().getFotoPerfil() : null
		);

		if (existingProfile.isPresent()) {
			jdbcTemplate.update("""
				update public.professores
				set pessoa_id = :pessoaId,
					bio = :bio,
					ativo = :ativo,
					diploma = :diploma,
					status_verificacao = :statusVerificacao,
					valor_hora_aula = :valorHoraAula,
					updated_at = now()
				where id = :id
				""", new MapSqlParameterSource()
				.addValue("id", existingProfile.get().id())
				.addValue("pessoaId", pessoaId)
				.addValue("bio", professor.getBio())
				.addValue("ativo", professor.isAtivo())
				.addValue("diploma", professor.getDiploma())
				.addValue("statusVerificacao", resolveStatusVerificacao(professor))
				.addValue("valorHoraAula", professor.getValorHoraAula()));
		} else {
			jdbcTemplate.update("""
				insert into public.professores (
					id,
					pessoa_id,
					bio,
					ativo,
					diploma,
					status_verificacao,
					valor_hora_aula,
					created_at,
					updated_at
				)
				values (
					:id,
					:pessoaId,
					:bio,
					:ativo,
					:diploma,
					:statusVerificacao,
					:valorHoraAula,
					now(),
					now()
				)
				""", new MapSqlParameterSource()
				.addValue("id", id)
				.addValue("pessoaId", pessoaId)
				.addValue("bio", professor.getBio())
				.addValue("ativo", professor.isAtivo())
				.addValue("diploma", professor.getDiploma())
				.addValue("statusVerificacao", resolveStatusVerificacao(professor))
				.addValue("valorHoraAula", professor.getValorHoraAula()));
		}

		return findProfessorByAuthUserId(professor.getAuthUserId()).orElseThrow();
	}

	@Override
	public Professor updateProfessorProfile(Professor professor) {
		var professorId = professor.getId() != null
			? professor.getId()
			: findProfessorProfileByAuthUserId(professor.getAuthUserId())
				.map(ProfileRow::id)
				.orElseThrow(() -> new IllegalArgumentException("Professor nao encontrado."));

		jdbcTemplate.update("""
			update public.professores
			set bio = :bio,
				ativo = :ativo,
				diploma = :diploma,
				valor_hora_aula = :valorHoraAula,
				updated_at = now()
			where id = :id
			""", new MapSqlParameterSource()
			.addValue("id", professorId)
			.addValue("bio", professor.getBio())
			.addValue("ativo", professor.isAtivo())
			.addValue("diploma", professor.getDiploma())
			.addValue("valorHoraAula", professor.getValorHoraAula()));

		return findProfessorById(professorId).orElseThrow();
	}

	private RowMapper<Aluno> alunoRowMapper() {
		return (rs, rowNum) -> {
			var aluno = new Aluno();
			aluno.setId(rs.getObject("id", UUID.class));
			aluno.setAuthUserId(rs.getObject("auth_user_id", UUID.class));
			aluno.setNome(rs.getString("nome"));
			aluno.setSobrenome(rs.getString("sobrenome"));
			var dataNascimento = rs.getObject("data_nascimento", LocalDate.class);
			aluno.setDataNascimento(dataNascimento);
			var genero = rs.getString("genero");
			aluno.setGenero(genero != null ? GeneroTipo.valueOf(genero.toUpperCase()) : GeneroTipo.NAO_INFORMADO);
			var enderecoId = rs.getObject("endereco_id", UUID.class);
			aluno.setEndereco(enderecoId != null ? enderecoFromId(enderecoId) : null);
			return aluno;
		};
	}

	private RowMapper<Professor> professorRowMapper() {
		return (rs, rowNum) -> {
			var professor = new Professor();
			professor.setId(rs.getObject("id", UUID.class));
			professor.setAuthUserId(rs.getObject("auth_user_id", UUID.class));
			professor.setNome(rs.getString("nome"));
			professor.setSobrenome(rs.getString("sobrenome"));
			professor.setCpf(rs.getString("cpf"));
			professor.setDataNascimento(rs.getObject("data_nascimento", LocalDate.class));
			professor.setBio(rs.getString("bio"));
			professor.setAtivo(rs.getBoolean("ativo"));
			professor.setDiploma(rs.getString("diploma"));
			professor.setStatusVerificacao(rs.getString("status_verificacao"));
			professor.setValorHoraAula(rs.getBigDecimal("valor_hora_aula"));
			var enderecoId = rs.getObject("endereco_id", UUID.class);
			professor.setEndereco(enderecoId != null ? enderecoFromId(enderecoId) : null);
			return professor;
		};
	}

	private Endereco enderecoFromId(UUID enderecoId) {
		try {
			return jdbcTemplate.queryForObject("""
				select id, rua, numero, complemento, bairro, cidade, estado, cep, pais
				from public.enderecos
				where id = :id
				""", new MapSqlParameterSource("id", enderecoId), (rs, rowNum) -> {
				var endereco = new Endereco();
				endereco.setId(rs.getObject("id", UUID.class));
				endereco.setRua(rs.getString("rua"));
				endereco.setNumero(rs.getString("numero"));
				endereco.setComplemento(rs.getString("complemento"));
				endereco.setBairro(rs.getString("bairro"));
				endereco.setCidade(rs.getString("cidade"));
				endereco.setEstado(rs.getString("estado"));
				endereco.setCep(rs.getString("cep"));
				endereco.setPais(rs.getString("pais"));
				return endereco;
			});
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return null;
		}
	}

	private Optional<ProfileRow> findAlunoProfileByAuthUserId(UUID authUserId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select a.id as profile_id, a.pessoa_id
				from public.alunos a
				join public.pessoas p on p.id = a.pessoa_id
				where p.auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), (rs, rowNum) ->
				new ProfileRow(rs.getObject("profile_id", UUID.class), rs.getObject("pessoa_id", UUID.class))
			));
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	private Optional<ProfileRow> findProfessorProfileByAuthUserId(UUID authUserId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select pr.id as profile_id, pr.pessoa_id
				from public.professores pr
				join public.pessoas p on p.id = pr.pessoa_id
				where p.auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), (rs, rowNum) ->
				new ProfileRow(rs.getObject("profile_id", UUID.class), rs.getObject("pessoa_id", UUID.class))
			));
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	private Optional<Professor> findProfessorById(UUID professorId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select pr.id,
					   p.auth_user_id,
					   p.nome,
					   p.sobrenome,
					   null as cpf,
					   p.data_nascimento,
					   pr.bio,
					   pr.ativo,
					   pr.diploma,
					   pr.status_verificacao,
					   pr.valor_hora_aula,
					   p.endereco_id
				from public.professores pr
				join public.pessoas p on p.id = pr.pessoa_id
				where pr.id = :id
				""", new MapSqlParameterSource("id", professorId), professorRowMapper()));
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	private String resolveStatusVerificacao(Professor professor) {
		return professor.getStatusVerificacao() != null ? professor.getStatusVerificacao() : "PENDENTE";
	}

	private record ProfileRow(UUID id, UUID pessoaId) {
	}
}
