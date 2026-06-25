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
	private final PessoaJdbcRepository pessoaJdbcRepository;

	public LocalProfileCadastroRepository(
		AlunoRepository alunoRepository,
		ProfessorRepository professorRepository,
		PessoaJdbcRepository pessoaJdbcRepository
	) {
		this.alunoRepository = alunoRepository;
		this.professorRepository = professorRepository;
		this.pessoaJdbcRepository = pessoaJdbcRepository;
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
	public Professor ensureProfessorByAuthUserId(UUID authUserId, Professor professorFallback) {
		return findProfessorByAuthUserId(authUserId).orElseGet(() -> {
			var pessoa = pessoaJdbcRepository.findByAuthUserId(authUserId).orElseGet(() -> {
				if (professorFallback == null || professorFallback.getPessoa() == null) {
					throw new IllegalArgumentException("Pessoa nao encontrada.");
				}

				var fallbackPessoa = professorFallback.getPessoa();
				fallbackPessoa.setId(pessoaJdbcRepository.ensureMinimalPessoa(
					authUserId,
					fallbackPessoa.getNome(),
					fallbackPessoa.getSobrenome(),
					fallbackPessoa.getDataNascimento(),
					fallbackPessoa.getGenero()
				));
				return fallbackPessoa;
			});
			var professor = new Professor();
			professor.setPessoa(pessoa);
			professor.setBio(null);
			professor.setAtivo(true);
			professor.setStatusVerificacao("PENDENTE");
			return professorRepository.save(professor);
		});
	}

	@Override
	public Aluno saveAluno(Aluno aluno) {
		return alunoRepository.save(aluno);
	}

	@Override
	public Professor saveProfessor(Professor professor) {
		return professorRepository.save(professor);
	}

	@Override
	public Professor updateProfessorProfile(Professor professor) {
		return professorRepository.updateProfile(professor);
	}
}
