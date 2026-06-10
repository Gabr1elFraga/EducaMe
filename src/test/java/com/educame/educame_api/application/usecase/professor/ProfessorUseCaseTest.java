package com.educame.educame_api.application.usecase.professor;

import com.educame.educame_api.application.dto.professor.ProfessorRequest;
import com.educame.educame_api.domain.contract.EnderecoRepository;
import com.educame.educame_api.domain.contract.ProfessorRepository;
import com.educame.educame_api.domain.endereco.Endereco;
import com.educame.educame_api.domain.professor.Professor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ProfessorUseCaseTest {
	@Mock
	private ProfessorRepository professorRepository;

	@Mock
	private EnderecoRepository enderecoRepository;

	@InjectMocks
	private ProfessorUseCase professorUseCase;

	@Test
	void shouldCreateProfessorWithResolvedEndereco() {
		var enderecoId = UUID.randomUUID();
		var endereco = endereco(enderecoId);
		var request = new ProfessorRequest(UUID.randomUUID(), "Ana", "Silva", "Bio", enderecoId, true);
		when(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(endereco));
		when(professorRepository.save(any(Professor.class))).thenAnswer(invocation -> invocation.getArgument(0));

		var response = professorUseCase.create(request);

		assertNotNull(response);
		assertEquals("Ana", response.nome());
		assertEquals(enderecoId, response.endereco().id());
		verify(professorRepository).save(any(Professor.class));
	}

	@Test
	void shouldThrowWhenProfessorNotFoundOnDelete() {
		var id = UUID.randomUUID();
		when(professorRepository.existsById(id)).thenReturn(false);

		var exception = assertThrows(ResponseStatusException.class, () -> professorUseCase.delete(id));

		assertEquals(NOT_FOUND, exception.getStatusCode());
	}

	private Endereco endereco(UUID id) {
		var endereco = new Endereco();
		endereco.setId(id);
		endereco.setRua("Rua A");
		endereco.setNumero("10");
		endereco.setComplemento("Casa");
		endereco.setBairro("Centro");
		endereco.setCidade("Cidade");
		endereco.setEstado("SP");
		endereco.setCep("00000-000");
		endereco.setPais("Brasil");
		return endereco;
	}
}
