package com.educame.educame_api.application.usecase.autenticacao;

import com.educame.educame_api.application.dto.autenticacao.CadastroAlunoRequest;
import com.educame.educame_api.application.dto.autenticacao.CadastroProfessorRequest;
import com.educame.educame_api.domain.aluno.Aluno;
import com.educame.educame_api.domain.contract.AlunoRepository;
import com.educame.educame_api.domain.contract.ProfessorRepository;
import com.educame.educame_api.domain.enums.GeneroTipo;
import com.educame.educame_api.domain.professor.Professor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacaoUseCaseTest {
	@Mock
	private AlunoRepository alunoRepository;

	@Mock
	private ProfessorRepository professorRepository;

	@InjectMocks
	private AutenticacaoUseCase autenticacaoUseCase;

	@Test
	void shouldRegisterAlunoLinkedToAuthUser() {
		var authUserId = UUID.randomUUID();
		var request = new CadastroAlunoRequest(
			authUserId,
			"Maria",
			"Silva",
			LocalDate.now().minusYears(17)
		);

		when(alunoRepository.findByAuthUserId(authUserId)).thenReturn(Optional.empty());
		when(professorRepository.findByAuthUserId(authUserId)).thenReturn(Optional.empty());
		when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = autenticacaoUseCase.cadastrarAluno(request);

		assertThat(response.authUserId()).isEqualTo(authUserId);
		assertThat(response.nome()).isEqualTo("Maria");
		assertThat(response.sobrenome()).isEqualTo("Silva");

		var captor = ArgumentCaptor.forClass(Aluno.class);
		verify(alunoRepository).save(captor.capture());
		assertThat(captor.getValue().getAuthUserId()).isEqualTo(authUserId);
		assertThat(captor.getValue().getGenero()).isEqualTo(GeneroTipo.NAO_INFORMADO);
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

		when(alunoRepository.findByAuthUserId(authUserId)).thenReturn(Optional.empty());
		when(professorRepository.findByAuthUserId(authUserId)).thenReturn(Optional.empty());
		when(professorRepository.save(any(Professor.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = autenticacaoUseCase.cadastrarProfessor(request);

		assertThat(response.authUserId()).isEqualTo(authUserId);
		assertThat(response.nome()).isEqualTo("Joao");
		assertThat(response.cpf()).isEqualTo("12345678910");

		var captor = ArgumentCaptor.forClass(Professor.class);
		verify(professorRepository).save(captor.capture());
		assertThat(captor.getValue().getAuthUserId()).isEqualTo(authUserId);
		assertThat(captor.getValue().getCpf()).isEqualTo("12345678910");
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

		when(alunoRepository.findByAuthUserId(authUserId)).thenReturn(Optional.empty());

		var exception = assertThrows(ResponseStatusException.class, () -> autenticacaoUseCase.cadastrarProfessor(request));

		assertThat(exception.getStatusCode().value()).isEqualTo(400);
		assertThat(exception.getReason()).contains("menor de idade");
	}
}
