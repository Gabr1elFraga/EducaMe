package com.educame.educame_api.infrastructure.persistence.jdbc;

import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.contract.AlunoRepository;
import com.educame.educame_api.domain.contract.ProfileCadastroRepository;
import com.educame.educame_api.domain.contract.ProfessorRepository;
import com.educame.educame_api.domain.professor.Professor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class LocalProfileCadastroRepository implements ProfileCadastroRepository {
	private final AlunoRepository alunoRepository;
	private final ProfessorRepository professorRepository;

	public LocalProfileCadastroRepository(AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
		this.alunoRepository = alunoRepository;
		this.professorRepository = professorRepository;
	}

	@Override
	public Optional<Aluno> findAlunoByAuthUserId(UUID authUserId) {
		return alunoRepository.findByAuthUserId(authUserId);
	}

	@Override
	public Optional<Professor> findProfessorByAuthUserId(UUID authUserId) {
		return professorRepository.findByAuthUserId(authUserId);
	}

	@Override
	public Aluno saveAluno(Aluno aluno) {
		return alunoRepository.save(aluno);
	}

	@Override
	public Professor saveProfessor(Professor professor) {
		return professorRepository.save(professor);
	}
}
