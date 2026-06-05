package com.educame.educame_api.domain.contract;

import com.educame.educame_api.domain.endereco.Endereco;

import java.util.Optional;
import java.util.UUID;

public interface EnderecoRepository {
	Optional<Endereco> findById(UUID id);
}
