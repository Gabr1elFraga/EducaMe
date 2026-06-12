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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AlunoJdbcRepository implements AlunoRepository {
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final EnderecoRepository enderecoRepository;
	private final PessoaJdbcRepository pessoaJdbcRepository;

	public AlunoJdbcRepository(
		NamedParameterJdbcTemplate jdbcTemplate,
		EnderecoRepository enderecoRepository,
		PessoaJdbcRepository pessoaJdbcRepository
	) {
		this.jdbcTemplate = jdbcTemplate;
		this.enderecoRepository = enderecoRepository;
		this.pessoaJdbcRepository = pessoaJdbcRepository;
	}

	@Override
	public List<Aluno> findAll() {
		return jdbcTemplate.query("""
			select a.id,
				   p.auth_user_id,
				   p.nome,
				   p.sobrenome,
				   p.data_nascimento,
				   p.genero,
				   p.endereco_id
			from alunos a
			join pessoas p on p.id = a.pessoa_id
			order by a.created_at desc
			""", alunoRowMapper());
	}

	@Override
	public Optional<Aluno> findById(UUID id) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select a.id,
					   p.auth_user_id,
					   p.nome,
					   p.sobrenome,
					   p.data_nascimento,
					   p.genero,
					   p.endereco_id
				from alunos a
				join pessoas p on p.id = a.pessoa_id
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
				select a.id,
					   p.auth_user_id,
					   p.nome,
					   p.sobrenome,
					   p.data_nascimento,
					   p.genero,
					   p.endereco_id
				from alunos a
				join pessoas p on p.id = a.pessoa_id
				where p.auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), alunoRowMapper()));
		} catch (EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Aluno save(Aluno aluno) {
		var id = aluno.getId() != null ? aluno.getId() : UUID.randomUUID();
		var existingPessoaId = findPessoaIdByAlunoId(id).orElse(null);
		var pessoaId = pessoaJdbcRepository.upsertPessoa(
			existingPessoaId,
			aluno.getAuthUserId(),
			aluno.getNome(),
			aluno.getSobrenome(),
			aluno.getDataNascimento(),
			aluno.getGenero(),
			aluno.getEndereco() != null ? aluno.getEndereco().getId() : null,
			null,
			null
		);

		if (existsById(id)) {
			jdbcTemplate.update("""
				update alunos
				set pessoa_id = :pessoaId,
					updated_at = now()
				where id = :id
				""", new MapSqlParameterSource()
				.addValue("id", id)
				.addValue("pessoaId", pessoaId));
		} else {
			jdbcTemplate.update("""
				insert into alunos (id, pessoa_id, created_at, updated_at)
				values (:id, :pessoaId, now(), now())
				""", new MapSqlParameterSource()
				.addValue("id", id)
				.addValue("pessoaId", pessoaId));
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

	private Optional<UUID> findPessoaIdByAlunoId(UUID alunoId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select pessoa_id
				from alunos
				where id = :id
				""", new MapSqlParameterSource("id", alunoId), UUID.class));
		} catch (EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
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
			aluno.setGenero(genero != null ? GeneroTipo.valueOf(genero.toUpperCase()) : null);
			var enderecoId = rs.getObject("endereco_id", UUID.class);
			aluno.setEndereco(enderecoId != null ? enderecoRepository.findById(enderecoId).orElse(null) : null);
			return aluno;
		};
	}
}
