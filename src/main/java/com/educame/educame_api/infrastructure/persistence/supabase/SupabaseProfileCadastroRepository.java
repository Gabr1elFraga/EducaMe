package com.educame.educame_api.infrastructure.persistence.supabase;

import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.contract.ProfileCadastroRepository;
import com.educame.educame_api.domain.enums.GeneroTipo;
import com.educame.educame_api.domain.professor.Professor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText('${SUPABASE_DB_URL:}')")
@Primary
public class SupabaseProfileCadastroRepository implements ProfileCadastroRepository {
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public SupabaseProfileCadastroRepository(
		@Value("${SUPABASE_DB_URL}") String url,
		@Value("${SUPABASE_DB_USER}") String user,
		@Value("${SUPABASE_DB_PASSWORD}") String password
	) {
		var dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public Optional<Aluno> findAlunoByAuthUserId(UUID authUserId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select id, auth_user_id, nome, sobrenome, data_nascimento, genero
				from public.alunos
				where auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), alunoRowMapper()));
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Professor> findProfessorByAuthUserId(UUID authUserId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select id, auth_user_id, nome, sobrenome, cpf, data_nascimento, bio, ativo
				from public.professores
				where auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), professorRowMapper()));
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Aluno saveAluno(Aluno aluno) {
		var id = aluno.getId() != null ? aluno.getId() : UUID.randomUUID();
		var params = new MapSqlParameterSource()
			.addValue("id", id)
			.addValue("authUserId", aluno.getAuthUserId())
			.addValue("nome", aluno.getNome())
			.addValue("sobrenome", aluno.getSobrenome())
			.addValue("dataNascimento", aluno.getDataNascimento())
			.addValue("genero", aluno.getGenero() != null ? aluno.getGenero().name() : GeneroTipo.NAO_INFORMADO.name())
			.addValue("updatedAt", OffsetDateTime.now());

		if (findAlunoByAuthUserId(aluno.getAuthUserId()).isPresent()) {
			jdbcTemplate.update("""
				update public.alunos
				set nome = :nome,
					sobrenome = :sobrenome,
					data_nascimento = :dataNascimento,
					genero = :genero,
					updated_at = :updatedAt
				where auth_user_id = :authUserId
				""", params);
			return findAlunoByAuthUserId(aluno.getAuthUserId()).orElseThrow();
		}

		params.addValue("createdAt", params.getValue("updatedAt"));
		jdbcTemplate.update("""
			insert into public.alunos (id, auth_user_id, nome, sobrenome, data_nascimento, genero, created_at, updated_at)
			values (:id, :authUserId, :nome, :sobrenome, :dataNascimento, :genero, :createdAt, :updatedAt)
			""", params);
		return findAlunoByAuthUserId(aluno.getAuthUserId()).orElseThrow();
	}

	@Override
	public Professor saveProfessor(Professor professor) {
		var id = professor.getId() != null ? professor.getId() : UUID.randomUUID();
		var params = new MapSqlParameterSource()
			.addValue("id", id)
			.addValue("authUserId", professor.getAuthUserId())
			.addValue("nome", professor.getNome())
			.addValue("sobrenome", professor.getSobrenome())
			.addValue("cpf", professor.getCpf())
			.addValue("dataNascimento", professor.getDataNascimento())
			.addValue("bio", professor.getBio())
			.addValue("ativo", professor.isAtivo())
			.addValue("updatedAt", OffsetDateTime.now());

		if (findProfessorByAuthUserId(professor.getAuthUserId()).isPresent()) {
			jdbcTemplate.update("""
				update public.professores
				set nome = :nome,
					sobrenome = :sobrenome,
					cpf = :cpf,
					data_nascimento = :dataNascimento,
					bio = :bio,
					ativo = :ativo,
					updated_at = :updatedAt
				where auth_user_id = :authUserId
				""", params);
			return findProfessorByAuthUserId(professor.getAuthUserId()).orElseThrow();
		}

		params.addValue("createdAt", params.getValue("updatedAt"));
		jdbcTemplate.update("""
			insert into public.professores (id, auth_user_id, nome, sobrenome, cpf, data_nascimento, bio, ativo, created_at, updated_at)
			values (:id, :authUserId, :nome, :sobrenome, :cpf, :dataNascimento, :bio, :ativo, :createdAt, :updatedAt)
			""", params);
		return findProfessorByAuthUserId(professor.getAuthUserId()).orElseThrow();
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
			aluno.setGenero(genero != null ? GeneroTipo.valueOf(genero) : GeneroTipo.NAO_INFORMADO);
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
			return professor;
		};
	}
}
