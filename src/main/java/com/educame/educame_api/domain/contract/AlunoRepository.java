package com.educame.educame_api.domain.contract;

import com.educame.educame_api.domain.aluno.Aluno;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlunoRepository {
	List<Aluno> findAll();

	Optional<Aluno> findById(UUID id);

	Aluno save(Aluno aluno);

	boolean existsById(UUID id);

	void deleteById(UUID id);
}
