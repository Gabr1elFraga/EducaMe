package com.educame.educame_api.application.usecase.professor;

import com.educame.educame_api.application.dto.professor.AtualizarProfessorPerfilRequest;
import com.educame.educame_api.application.mapper.DomainEntityMapper;
import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.contract.ProfileCadastroRepository;
import com.educame.educame_api.domain.pessoa.Pessoa;
import com.educame.educame_api.domain.professor.Professor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProfessorPerfilUseCaseTest {
	private InMemoryProfileCadastroRepository profileCadastroRepository;
	private ProfessorPerfilUseCase professorPerfilUseCase;

	@BeforeEach
	void setUp() {
		profileCadastroRepository = new InMemoryProfileCadastroRepository();
		DomainEntityMapper domainEntityMapper = Mappers.getMapper(DomainEntityMapper.class);
		professorPerfilUseCase = new ProfessorPerfilUseCase(profileCadastroRepository, domainEntityMapper);
	}

	@Test
	void shouldReturnAuthenticatedProfessorProfile() {
		var authUserId = UUID.randomUUID();
		profileCadastroRepository.savedProfessor = buildProfessor(authUserId);

		var response = professorPerfilUseCase.buscarPerfil(authUserId, null);

		assertThat(response.authUserId()).isEqualTo(authUserId);
		assertThat(response.nome()).isEqualTo("Carlos");
		assertThat(response.bio()).isEqualTo("Professor de matematica");
		assertThat(response.ativo()).isTrue();
	}

	@Test
	void shouldUpdateOnlyProfessorProfileFields() {
		var authUserId = UUID.randomUUID();
		profileCadastroRepository.savedProfessor = buildProfessor(authUserId);

		var response = professorPerfilUseCase.atualizarPerfil(
			authUserId,
			null,
			new AtualizarProfessorPerfilRequest("  Nova bio  ", false, "  Diploma em matematica  ", BigDecimal.valueOf(80))
		);

		assertThat(response.bio()).isEqualTo("Nova bio");
		assertThat(response.ativo()).isFalse();
		assertThat(response.diploma()).isEqualTo("Diploma em matematica");
		assertThat(response.valorHoraAula()).isEqualByComparingTo("80");
		assertThat(profileCadastroRepository.updatedProfessor.getBio()).isEqualTo("Nova bio");
		assertThat(profileCadastroRepository.updatedProfessor.isAtivo()).isFalse();
		assertThat(profileCadastroRepository.updatedProfessor.getDiploma()).isEqualTo("Diploma em matematica");
		assertThat(profileCadastroRepository.updatedProfessor.getValorHoraAula()).isEqualByComparingTo("80");
		assertThat(profileCadastroRepository.updatedProfessor.getNome()).isEqualTo("Carlos");
		assertThat(profileCadastroRepository.updatedProfessor.getCpf()).isEqualTo("12345678910");
	}

	@Test
	void shouldCreateProfessorProfileWhenPessoaExists() {
		var authUserId = UUID.randomUUID();
		profileCadastroRepository.savedPessoa = buildProfessor(authUserId).getPessoa();

		var response = professorPerfilUseCase.buscarPerfil(authUserId, null);

		assertThat(response.authUserId()).isEqualTo(authUserId);
		assertThat(response.nome()).isEqualTo("Carlos");
		assertThat(response.bio()).isNull();
		assertThat(response.ativo()).isTrue();
		assertThat(profileCadastroRepository.savedProfessor).isNotNull();
	}

	@Test
	void shouldCreatePessoaAndProfessorProfileFromFallback() {
		var authUserId = UUID.randomUUID();
		var fallback = buildProfessor(authUserId);

		var response = professorPerfilUseCase.buscarPerfil(authUserId, fallback);

		assertThat(response.authUserId()).isEqualTo(authUserId);
		assertThat(response.nome()).isEqualTo("Carlos");
		assertThat(response.sobrenome()).isEqualTo("Santos");
		assertThat(response.ativo()).isTrue();
		assertThat(profileCadastroRepository.savedPessoa).isNotNull();
		assertThat(profileCadastroRepository.savedProfessor).isNotNull();
	}

	@Test
	void shouldReturnNotFoundWhenPessoaDoesNotExist() {
		var exception = assertThrows(
			ResponseStatusException.class,
			() -> professorPerfilUseCase.buscarPerfil(UUID.randomUUID(), null)
		);

		assertThat(exception.getStatusCode().value()).isEqualTo(404);
	}

	private Professor buildProfessor(UUID authUserId) {
		var pessoa = new Pessoa();
		pessoa.setAuthUserId(authUserId);
		pessoa.setNome("Carlos");
		pessoa.setSobrenome("Santos");
		pessoa.setCpf("12345678910");
		pessoa.setDataNascimento(LocalDate.now().minusYears(31));

		var professor = new Professor();
		professor.setId(UUID.randomUUID());
		professor.setPessoa(pessoa);
		professor.setBio("Professor de matematica");
		professor.setAtivo(true);
		professor.setStatusVerificacao("PENDENTE");
		return professor;
	}

	private static final class InMemoryProfileCadastroRepository implements ProfileCadastroRepository {
		private Pessoa savedPessoa;
		private Professor savedProfessor;
		private Professor updatedProfessor;

		@Override
		public Optional<Aluno> findAlunoByAuthUserId(UUID authUserId) {
			return Optional.empty();
		}

		@Override
		public Optional<Professor> findProfessorByAuthUserId(UUID authUserId) {
			return Optional.ofNullable(savedProfessor)
				.filter(professor -> authUserId.equals(professor.getAuthUserId()));
		}

		@Override
		public Professor ensureProfessorByAuthUserId(UUID authUserId, Professor professorFallback) {
			return findProfessorByAuthUserId(authUserId).orElseGet(() -> {
				if (savedPessoa == null && professorFallback != null) {
					savedPessoa = professorFallback.getPessoa();
				}

				if (savedPessoa == null || !authUserId.equals(savedPessoa.getAuthUserId())) {
					throw new IllegalArgumentException("Pessoa nao encontrada.");
				}

				var professor = new Professor();
				professor.setId(UUID.randomUUID());
				professor.setPessoa(savedPessoa);
				professor.setBio(null);
				professor.setAtivo(true);
				professor.setStatusVerificacao("PENDENTE");
				savedProfessor = professor;
				return professor;
			});
		}

		@Override
		public Aluno saveAluno(Aluno aluno) {
			return aluno;
		}

		@Override
		public Professor saveProfessor(Professor professor) {
			savedProfessor = professor;
			return professor;
		}

		@Override
		public Professor updateProfessorProfile(Professor professor) {
			updatedProfessor = professor;
			savedProfessor = professor;
			return professor;
		}
	}
}
