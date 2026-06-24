package com.educame.educame_api.application.usecase.autenticacao;

import com.educame.educame_api.application.dto.autenticacao.CadastroAlunoRequest;
import com.educame.educame_api.application.dto.autenticacao.CadastroProfessorRequest;
import com.educame.educame_api.application.mapper.DomainEntityMapper;
import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.contract.ProfileCadastroRepository;
import com.educame.educame_api.domain.enums.GeneroTipo;
import com.educame.educame_api.domain.endereco.Endereco;
import com.educame.educame_api.domain.professor.Professor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AutenticacaoUseCaseTest {
	private InMemoryProfileCadastroRepository profileCadastroRepository;
	private AutenticacaoUseCase autenticacaoUseCase;

	@BeforeEach
	void setUp() {
		profileCadastroRepository = new InMemoryProfileCadastroRepository();
		DomainEntityMapper domainEntityMapper = Mappers.getMapper(DomainEntityMapper.class);
		autenticacaoUseCase = new AutenticacaoUseCase(profileCadastroRepository, domainEntityMapper);
	}

	@Test
	void shouldRegisterAlunoLinkedToAuthUser() {
		var authUserId = UUID.randomUUID();
		var request = new CadastroAlunoRequest(
			authUserId,
			"Maria",
			"Silva",
			LocalDate.now().minusYears(17)
		);

		var response = autenticacaoUseCase.cadastrarAluno(request);

		assertThat(response.authUserId()).isEqualTo(authUserId);
		assertThat(response.nome()).isEqualTo("Maria");
		assertThat(response.sobrenome()).isEqualTo("Silva");
		assertThat(profileCadastroRepository.savedAluno).isNotNull();
		assertThat(profileCadastroRepository.savedAluno.getAuthUserId()).isEqualTo(authUserId);
		assertThat(profileCadastroRepository.savedAluno.getGenero()).isEqualTo(GeneroTipo.NAO_INFORMADO);
	}

	@Test
	void shouldRegisterProfessorLinkedToAuthUserWhenAdult() {
		var authUserId = UUID.randomUUID();
		var request = new CadastroProfessorRequest(
			authUserId,
			"Joao",
			"Souza",
			"123.456.789-10",
			LocalDate.now().minusYears(22)
		);

		var response = autenticacaoUseCase.cadastrarProfessor(request);

		assertThat(response.authUserId()).isEqualTo(authUserId);
		assertThat(response.nome()).isEqualTo("Joao");
		assertThat(response.cpf()).isEqualTo("12345678910");
		assertThat(profileCadastroRepository.savedProfessor).isNotNull();
		assertThat(profileCadastroRepository.savedProfessor.getAuthUserId()).isEqualTo(authUserId);
		assertThat(profileCadastroRepository.savedProfessor.getCpf()).isEqualTo("12345678910");
	}

	@Test
	void shouldRejectProfessorRegistrationForMinor() {
		var authUserId = UUID.randomUUID();
		var request = new CadastroProfessorRequest(
			authUserId,
			"Ana",
			"Lima",
			"12345678910",
			LocalDate.now().minusYears(17)
		);

		var exception = assertThrows(ResponseStatusException.class, () -> autenticacaoUseCase.cadastrarProfessor(request));

		assertThat(exception.getStatusCode().value()).isEqualTo(400);
		assertThat(exception.getReason()).contains("menor de idade");
	}

	@Test
	void shouldAllowSameUserToRegisterAlunoAndProfessor() {
		var authUserId = UUID.randomUUID();
		var alunoRequest = new CadastroAlunoRequest(
			authUserId,
			"Maria",
			"Silva",
			LocalDate.now().minusYears(17)
		);
		var professorRequest = new CadastroProfessorRequest(
			authUserId,
			"Maria",
			"Silva",
			"123.456.789-10",
			LocalDate.now().minusYears(22)
		);

		var alunoResponse = autenticacaoUseCase.cadastrarAluno(alunoRequest);
		var professorResponse = autenticacaoUseCase.cadastrarProfessor(professorRequest);

		assertThat(alunoResponse.authUserId()).isEqualTo(authUserId);
		assertThat(professorResponse.authUserId()).isEqualTo(authUserId);
		assertThat(profileCadastroRepository.savedAluno).isNotNull();
		assertThat(profileCadastroRepository.savedProfessor).isNotNull();
	}

	private static final class InMemoryProfileCadastroRepository implements ProfileCadastroRepository {
		private Aluno savedAluno;
		private Professor savedProfessor;

		@Override
		public Optional<Aluno> findAlunoByAuthUserId(UUID authUserId) {
			return Optional.ofNullable(savedAluno).filter(aluno -> authUserId.equals(aluno.getAuthUserId()));
		}

		@Override
		public Optional<Professor> findProfessorByAuthUserId(UUID authUserId) {
			return Optional.ofNullable(savedProfessor).filter(professor -> authUserId.equals(professor.getAuthUserId()));
		}

		@Override
		public Professor ensureProfessorByAuthUserId(UUID authUserId, Professor professorFallback) {
			return findProfessorByAuthUserId(authUserId).orElseGet(() -> {
				var professor = new Professor();
				professor.setAuthUserId(authUserId);
				professor.setAtivo(true);
				professor.setStatusVerificacao("PENDENTE");
				return saveProfessor(professor);
			});
		}

		@Override
		public Aluno saveAluno(Aluno aluno) {
			savedAluno = aluno;
			if (aluno.getId() == null) {
				aluno.setId(UUID.randomUUID());
			}
			if (aluno.getGenero() == null) {
				aluno.setGenero(GeneroTipo.NAO_INFORMADO);
			}
			return aluno;
		}

		@Override
		public Professor saveProfessor(Professor professor) {
			savedProfessor = professor;
			if (professor.getId() == null) {
				professor.setId(UUID.randomUUID());
			}
			if (professor.getEndereco() == null) {
				professor.setEndereco(new Endereco());
			}
			return professor;
		}

		@Override
		public Professor updateProfessorProfile(Professor professor) {
			savedProfessor = professor;
			return professor;
		}
	}
}
