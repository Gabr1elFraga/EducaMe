package com.educame.educame_api.infrastructure.persistence.jdbc;

import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.contract.AlunoRepository;
import com.educame.educame_api.domain.contract.EnderecoRepository;
import com.educame.educame_api.domain.enums.GeneroTipo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AlunoJdbcRepository implements AlunoRepository {
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final EnderecoRepository enderecoRepository;

	public AlunoJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate, EnderecoRepository enderecoRepository) {
		this.jdbcTemplate = jdbcTemplate;
		this.enderecoRepository = enderecoRepository;
	}

	@Override
	public List<Aluno> findAll() {
		return jdbcTemplate.query("""
			select id, auth_user_id, nome, sobrenome, data_nascimento, genero, endereco_id
			from alunos
			order by created_at desc
			""", alunoRowMapper());
	}

	@Override
	public Optional<Aluno> findById(UUID id) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select id, auth_user_id, nome, sobrenome, data_nascimento, genero, endereco_id
				from alunos
				where id = :id
				""", new MapSqlParameterSource("id", id), alunoRowMapper()));
		} catch (EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Aluno> findByAuthUserId(UUID authUserId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select id, auth_user_id, nome, sobrenome, data_nascimento, genero, endereco_id
				from alunos
				where auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), alunoRowMapper()));
		} catch (EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Aluno save(Aluno aluno) {
		var id = aluno.getId() != null ? aluno.getId() : UUID.randomUUID();
		var params = new MapSqlParameterSource()
			.addValue("id", id)
			.addValue("authUserId", aluno.getAuthUserId())
			.addValue("nome", aluno.getNome())
			.addValue("sobrenome", aluno.getSobrenome())
			.addValue("dataNascimento", aluno.getDataNascimento())
			.addValue("genero", aluno.getGenero() != null ? aluno.getGenero().name() : null)
			.addValue("enderecoId", aluno.getEndereco() != null ? aluno.getEndereco().getId() : null)
			.addValue("updatedAt", OffsetDateTime.now());

		if (existsById(id)) {
			jdbcTemplate.update("""
				update alunos
				set auth_user_id = :authUserId,
					nome = :nome,
					sobrenome = :sobrenome,
					data_nascimento = :dataNascimento,
					genero = :genero,
					endereco_id = :enderecoId,
					updated_at = :updatedAt
				where id = :id
				""", params);
		} else {
			params.addValue("createdAt", params.getValue("updatedAt"));
			jdbcTemplate.update("""
				insert into alunos (id, auth_user_id, nome, sobrenome, data_nascimento, genero, endereco_id, created_at, updated_at)
				values (:id, :authUserId, :nome, :sobrenome, :dataNascimento, :genero, :enderecoId, :createdAt, :updatedAt)
				""", params);
		}

		return findById(id).orElseThrow();
	}

	@Override
	public boolean existsById(UUID id) {
		var count = jdbcTemplate.queryForObject(
			"select count(1) from alunos where id = :id",
			new MapSqlParameterSource("id", id),
			Integer.class
		);
		return count != null && count > 0;
	}

	@Override
	public void deleteById(UUID id) {
		jdbcTemplate.update("delete from alunos where id = :id", new MapSqlParameterSource("id", id));
	}

	private RowMapper<Aluno> alunoRowMapper() {
		return (rs, rowNum) -> {
			var aluno = new Aluno();
			aluno.setId(rs.getObject("id", UUID.class));
			aluno.setAuthUserId(rs.getObject("auth_user_id", UUID.class));
			aluno.setNome(rs.getString("nome"));
			aluno.setSobrenome(rs.getString("sobrenome"));
			var dataNascimento = rs.getDate("data_nascimento");
			aluno.setDataNascimento(dataNascimento != null ? dataNascimento.toLocalDate() : null);
			var genero = rs.getString("genero");
			aluno.setGenero(genero != null ? GeneroTipo.valueOf(genero) : null);
			var enderecoId = rs.getObject("endereco_id", UUID.class);
			aluno.setEndereco(enderecoId != null ? enderecoRepository.findById(enderecoId).orElse(null) : null);
			return aluno;
		};
	}
}
