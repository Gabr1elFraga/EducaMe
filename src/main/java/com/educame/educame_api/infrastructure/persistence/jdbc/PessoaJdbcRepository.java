package com.educame.educame_api.infrastructure.persistence.jdbc;

import com.educame.educame_api.domain.pessoa.Pessoa;
import com.educame.educame_api.domain.enums.GeneroTipo;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class PessoaJdbcRepository {
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public PessoaJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Optional<UUID> findIdByAuthUserId(UUID authUserId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select id
				from public.pessoas
				where auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), UUID.class));
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	public Optional<Pessoa> findByAuthUserId(UUID authUserId) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("""
				select id,
					   auth_user_id,
					   nome,
					   sobrenome,
					   data_nascimento,
					   genero
				from public.pessoas
				where auth_user_id = :authUserId
				""", new MapSqlParameterSource("authUserId", authUserId), (rs, rowNum) -> {
				var pessoa = new Pessoa();
				pessoa.setId(rs.getObject("id", UUID.class));
				pessoa.setAuthUserId(rs.getObject("auth_user_id", UUID.class));
				pessoa.setNome(rs.getString("nome"));
				pessoa.setSobrenome(rs.getString("sobrenome"));
				pessoa.setDataNascimento(rs.getObject("data_nascimento", LocalDate.class));
				var genero = rs.getString("genero");
				pessoa.setGenero(genero != null ? GeneroTipo.valueOf(genero.toUpperCase()) : GeneroTipo.NAO_INFORMADO);
				return pessoa;
			}));
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

	public UUID upsertPessoa(
		UUID pessoaId,
		UUID authUserId,
		String nome,
		String sobrenome,
		LocalDate dataNascimento,
		GeneroTipo genero,
		UUID enderecoId,
		String cpf,
		String fotoPerfil
	) {
		var now = OffsetDateTime.now();
		var existingId = pessoaId != null ? pessoaId : findIdByAuthUserId(authUserId).orElse(null);
		if (existingId != null) {
			var resolvedEnderecoId = enderecoId != null ? enderecoId : findEnderecoIdByPessoaId(existingId).orElse(null);
			jdbcTemplate.update("""
				update pessoas
				set auth_user_id = :authUserId,
					nome = :nome,
					sobrenome = :sobrenome,
					data_nascimento = :dataNascimento,
					endereco_id = :enderecoId,
					updated_at = :updatedAt
				where id = :id
				""", new MapSqlParameterSource()
				.addValue("id", existingId)
				.addValue("authUserId", authUserId)
				.addValue("nome", nome)
				.addValue("sobrenome", sobrenome)
				.addValue("dataNascimento", dataNascimento)
				.addValue("enderecoId", resolvedEnderecoId)
				.addValue("updatedAt", now));
			return existingId;
		}

		return jdbcTemplate.queryForObject("""
			insert into pessoas (
				auth_user_id,
				nome,
				sobrenome,
				data_nascimento,
				endereco_id,
				created_at,
				updated_at
			)
			values (
				:authUserId,
				:nome,
				:sobrenome,
				:dataNascimento,
				:enderecoId,
				:createdAt,
				:updatedAt
			)
			returning id
			""", new MapSqlParameterSource()
			.addValue("authUserId", authUserId)
			.addValue("nome", nome)
			.addValue("sobrenome", sobrenome)
			.addValue("dataNascimento", dataNascimento)
			.addValue("enderecoId", enderecoId)
			.addValue("createdAt", now)
			.addValue("updatedAt", now), UUID.class);
	}

	public UUID ensureMinimalPessoa(
		UUID authUserId,
		String nome,
		String sobrenome,
		LocalDate dataNascimento,
		GeneroTipo genero
	) {
		var existingId = findIdByAuthUserId(authUserId).orElse(null);
		if (existingId != null) {
			return existingId;
		}

		var now = OffsetDateTime.now();
		var columns = findPessoaColumns();
		var insertColumns = new ArrayList<String>();
		var insertValues = new ArrayList<String>();
		var params = new MapSqlParameterSource();

		insertColumns.add("auth_user_id");
		insertValues.add(":authUserId");
		params.addValue("authUserId", authUserId);

		addColumnIfExists(columns, insertColumns, insertValues, params, "nome", ":nome", "nome", nome);
		addColumnIfExists(columns, insertColumns, insertValues, params, "sobrenome", ":sobrenome", "sobrenome", sobrenome);
		addColumnIfExists(columns, insertColumns, insertValues, params, "data_nascimento", ":dataNascimento", "dataNascimento", dataNascimento);
		addColumnIfExists(columns, insertColumns, insertValues, params, "created_at", ":createdAt", "createdAt", now);
		addColumnIfExists(columns, insertColumns, insertValues, params, "updated_at", ":updatedAt", "updatedAt", now);

		var sql = "insert into public.pessoas (" +
			String.join(", ", insertColumns) +
			") values (" +
			String.join(", ", insertValues) +
			") returning id";

		return jdbcTemplate.queryForObject(sql, params, UUID.class);
	}

	private Optional<UUID> findEnderecoIdByPessoaId(UUID pessoaId) {
		return Optional.ofNullable(jdbcTemplate.queryForObject("""
			select endereco_id
			from pessoas
			where id = :id
			""", new MapSqlParameterSource("id", pessoaId), UUID.class));
	}

	private Set<String> findPessoaColumns() {
		return new HashSet<>(jdbcTemplate.queryForList("""
			select column_name
			from information_schema.columns
			where table_schema = 'public'
			  and table_name = 'pessoas'
			""", new MapSqlParameterSource(), String.class));
	}

	private void addColumnIfExists(
		Set<String> existingColumns,
		ArrayList<String> insertColumns,
		ArrayList<String> insertValues,
		MapSqlParameterSource params,
		String column,
		String valueExpression,
		String paramName,
		Object paramValue
	) {
		if (!existingColumns.contains(column)) {
			return;
		}

		insertColumns.add(column);
		insertValues.add(valueExpression);
		params.addValue(paramName, paramValue);
	}
}
