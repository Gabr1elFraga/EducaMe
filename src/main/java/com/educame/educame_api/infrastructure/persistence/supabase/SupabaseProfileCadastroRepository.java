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
				select id, auth_user_id, nome, sobrenome, "dataNascimento", genero
				from public."Aluno"
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
				select id, auth_user_id, nome, sobrenome, cpf, "dataNascimento", genero, "fotoPerfil", diploma, "statusVerificacao", "valorHoraAula"
				from public."Professor"
				where auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), professorRowMapper()));
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Aluno saveAluno(Aluno aluno) {
		var params = new MapSqlParameterSource()
			.addValue("authUserId", aluno.getAuthUserId())
			.addValue("nome", aluno.getNome())
			.addValue("sobrenome", aluno.getSobrenome())
			.addValue("dataNascimento", aluno.getDataNascimento());

		if (findAlunoByAuthUserId(aluno.getAuthUserId()).isPresent()) {
			jdbcTemplate.update("""
				update public."Aluno"
				set nome = :nome,
					sobrenome = :sobrenome,
					"dataNascimento" = :dataNascimento
				where auth_user_id = :authUserId
				""", params);
			return findAlunoByAuthUserId(aluno.getAuthUserId()).orElseThrow();
		}

		jdbcTemplate.update("""
			insert into public."Aluno" (auth_user_id, nome, sobrenome, "dataNascimento")
			values (:authUserId, :nome, :sobrenome, :dataNascimento)
			""", params);
		return findAlunoByAuthUserId(aluno.getAuthUserId()).orElseThrow();
	}

	@Override
	public Professor saveProfessor(Professor professor) {
		var params = new MapSqlParameterSource()
			.addValue("authUserId", professor.getAuthUserId())
			.addValue("nome", professor.getNome())
			.addValue("sobrenome", professor.getSobrenome())
			.addValue("cpf", professor.getCpf())
			.addValue("dataNascimento", professor.getDataNascimento())
			.addValue("fotoPerfil", null)
			.addValue("diploma", null)
			.addValue("statusVerificacao", "VALIDO")
			.addValue("valorHoraAula", 0);

		if (findProfessorByAuthUserId(professor.getAuthUserId()).isPresent()) {
			jdbcTemplate.update("""
				update public."Professor"
				set nome = :nome,
					sobrenome = :sobrenome,
					cpf = :cpf,
					"dataNascimento" = :dataNascimento,
					"fotoPerfil" = :fotoPerfil,
					diploma = :diploma,
					"statusVerificacao" = :statusVerificacao,
					"valorHoraAula" = :valorHoraAula
				where auth_user_id = :authUserId
				""", params);
			return findProfessorByAuthUserId(professor.getAuthUserId()).orElseThrow();
		}

		jdbcTemplate.update("""
			insert into public."Professor" (
				auth_user_id,
				nome,
				sobrenome,
				cpf,
				"dataNascimento",
				"fotoPerfil",
				diploma,
				"statusVerificacao",
				"valorHoraAula"
			)
			values (
				:authUserId,
				:nome,
				:sobrenome,
				:cpf,
				:dataNascimento,
				:fotoPerfil,
				:diploma,
				:statusVerificacao,
				:valorHoraAula
			)
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
			aluno.setDataNascimento(rs.getObject("dataNascimento", LocalDate.class));
			var genero = rs.getString("genero");
			aluno.setGenero(genero != null ? GeneroTipo.valueOf(genero) : null);
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
			professor.setDataNascimento(rs.getObject("dataNascimento", LocalDate.class));
			professor.setBio(null);
			professor.setAtivo(true);
			return professor;
		};
	}
}
