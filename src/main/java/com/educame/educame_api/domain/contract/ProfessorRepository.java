package com.educame.educame_api.domain.contract;

import com.educame.educame_api.domain.professor.Professor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessorRepository {
	List<Professor> findAll();

	Optional<Professor> findById(UUID id);

	Optional<Professor> findByAuthUserId(UUID authUserId);

	Professor save(Professor professor);

	boolean existsById(UUID id);

	void deleteById(UUID id);
}
