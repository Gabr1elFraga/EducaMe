package com.educame.educame_api.infrastructure.persistence.jdbc;

import com.educame.educame_api.domain.contract.EnderecoRepository;
import com.educame.educame_api.domain.endereco.Endereco;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class EnderecoJdbcRepository implements EnderecoRepository {
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public EnderecoJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Optional<Endereco> findById(UUID id) {
		try {
			var endereco = jdbcTemplate.queryForObject("""
				select id, rua, numero, complemento, bairro, cidade, estado, cep, pais
				from enderecos
				where id = :id
				""", new MapSqlParameterSource("id", id), (rs, rowNum) -> new Endereco(
				rs.getObject("id", UUID.class),
				rs.getString("rua"),
				rs.getString("numero"),
				rs.getString("complemento"),
				rs.getString("bairro"),
				rs.getString("cidade"),
				rs.getString("estado"),
				rs.getString("cep"),
				rs.getString("pais")
			));
			return Optional.ofNullable(endereco);
		} catch (EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}
}
