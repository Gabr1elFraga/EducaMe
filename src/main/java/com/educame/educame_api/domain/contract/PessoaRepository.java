package com.educame.educame_api.domain.contract;

import com.educame.educame_api.domain.pessoa.Pessoa;

import java.util.Optional;
import java.util.UUID;

public interface PessoaRepository {
	Optional<Pessoa> findByAuthUserId(UUID authUserId);

	Pessoa saveProfile(Pessoa pessoa);
}
