package com.educame.educame_api.domain.contract;

import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.professor.Professor;

import java.util.Optional;
import java.util.UUID;

public interface ProfileCadastroRepository {
	Optional<Aluno> findAlunoByAuthUserId(UUID authUserId);

	Optional<Professor> findProfessorByAuthUserId(UUID authUserId);

	Aluno saveAluno(Aluno aluno);

	Professor saveProfessor(Professor professor);
}
