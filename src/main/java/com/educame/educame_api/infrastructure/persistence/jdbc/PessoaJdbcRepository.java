package com.educame.educame_api.infrastructure.persistence.jdbc;

import com.educame.educame_api.domain.enums.GeneroTipo;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PessoaJdbcRepository {
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public PessoaJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Optional<UUID> findIdByAuthUserId(UUID authUserId) {
		return Optional.ofNullable(jdbcTemplate.queryForObject("""
			select id
			from pessoas
			where auth_user_id = :authUserId
			""", new MapSqlParameterSource("authUserId", authUserId), UUID.class));
	}

	public UUID upsertPessoa(
		UUID pessoaId,
		UUID authUserId,
		String nome,
		String sobrenome,
		LocalDate dataNascimento,
		GeneroTipo genero,
		UUID enderecoId
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
					genero = :genero,
					endereco_id = :enderecoId,
					updated_at = :updatedAt
				where id = :id
				""", new MapSqlParameterSource()
				.addValue("id", existingId)
				.addValue("authUserId", authUserId)
				.addValue("nome", nome)
				.addValue("sobrenome", sobrenome)
				.addValue("dataNascimento", dataNascimento)
				.addValue("genero", genero != null ? genero.name() : GeneroTipo.NAO_INFORMADO.name())
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
				genero,
				endereco_id,
				created_at,
				updated_at
			)
			values (
				:authUserId,
				:nome,
				:sobrenome,
				:dataNascimento,
				:genero,
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
			.addValue("genero", genero != null ? genero.name() : GeneroTipo.NAO_INFORMADO.name())
			.addValue("enderecoId", enderecoId)
			.addValue("createdAt", now)
			.addValue("updatedAt", now), UUID.class);
	}

	private Optional<UUID> findEnderecoIdByPessoaId(UUID pessoaId) {
		return Optional.ofNullable(jdbcTemplate.queryForObject("""
			select endereco_id
			from pessoas
			where id = :id
			""", new MapSqlParameterSource("id", pessoaId), UUID.class));
	}
}
