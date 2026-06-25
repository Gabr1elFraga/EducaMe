package com.educame.educame_api.presentation.controller;

import com.educame.educame_api.application.dto.professor.AtualizarProfessorPerfilRequest;
import com.educame.educame_api.application.dto.professor.ProfessorResponse;
import com.educame.educame_api.application.usecase.professor.ProfessorPerfilUseCase;
import com.educame.educame_api.domain.enums.GeneroTipo;
import com.educame.educame_api.domain.pessoa.Pessoa;
import com.educame.educame_api.domain.professor.Professor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/professores")
public class ProfessorController {
	private final ProfessorPerfilUseCase professorPerfilUseCase;

	public ProfessorController(ProfessorPerfilUseCase professorPerfilUseCase) {
		this.professorPerfilUseCase = professorPerfilUseCase;
	}

	@GetMapping("/me")
	public ResponseEntity<ProfessorResponse> buscarMeuPerfil(
		@AuthenticationPrincipal Jwt jwt,
		@RequestHeader(value = "X-Auth-User-Id", required = false) UUID authUserId,
		@RequestHeader(value = "X-User-Nome", required = false) String nome,
		@RequestHeader(value = "X-User-Sobrenome", required = false) String sobrenome,
		@RequestHeader(value = "X-User-Data-Nascimento", required = false) String dataNascimento,
		@RequestHeader(value = "X-User-Email", required = false) String email
	) {
		var resolvedAuthUserId = resolveAuthUserId(jwt, authUserId);
		return ResponseEntity.ok(professorPerfilUseCase.buscarPerfil(
			resolvedAuthUserId,
			buildProfessorFallback(resolvedAuthUserId, nome, sobrenome, dataNascimento, email)
		));
	}

	@PutMapping("/me")
	public ResponseEntity<ProfessorResponse> atualizarMeuPerfil(
		@AuthenticationPrincipal Jwt jwt,
		@RequestHeader(value = "X-Auth-User-Id", required = false) UUID authUserId,
		@RequestHeader(value = "X-User-Nome", required = false) String nome,
		@RequestHeader(value = "X-User-Sobrenome", required = false) String sobrenome,
		@RequestHeader(value = "X-User-Data-Nascimento", required = false) String dataNascimento,
		@RequestHeader(value = "X-User-Email", required = false) String email,
		@Valid @RequestBody AtualizarProfessorPerfilRequest request
	) {
		var resolvedAuthUserId = resolveAuthUserId(jwt, authUserId);
		return ResponseEntity.ok(professorPerfilUseCase.atualizarPerfil(
			resolvedAuthUserId,
			buildProfessorFallback(resolvedAuthUserId, nome, sobrenome, dataNascimento, email),
			request
		));
	}

	private UUID resolveAuthUserId(Jwt jwt, UUID authUserId) {
		if (jwt != null && jwt.getSubject() != null) {
			return UUID.fromString(jwt.getSubject());
		}

		if (authUserId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "O usuario autenticado e obrigatorio.");
		}

		return authUserId;
	}

	private Professor buildProfessorFallback(
		UUID authUserId,
		String nome,
		String sobrenome,
		String dataNascimento,
		String email
	) {
		var pessoa = new Pessoa();
		pessoa.setAuthUserId(authUserId);
		pessoa.setNome(resolveNome(nome, email));
		pessoa.setSobrenome(resolveSobrenome(sobrenome));
		pessoa.setDataNascimento(resolveDataNascimento(dataNascimento));
		pessoa.setGenero(GeneroTipo.NAO_INFORMADO);

		var professor = new Professor();
		professor.setPessoa(pessoa);
		professor.setAtivo(true);
		professor.setStatusVerificacao("PENDENTE");
		return professor;
	}

	private String resolveNome(String nome, String email) {
		var decodedNome = decodeHeader(nome);
		if (decodedNome != null && !decodedNome.isBlank()) {
			return decodedNome.trim();
		}

		var decodedEmail = decodeHeader(email);
		if (decodedEmail != null && decodedEmail.contains("@")) {
			return decodedEmail.substring(0, decodedEmail.indexOf('@'));
		}

		return "Nao informado";
	}

	private String resolveSobrenome(String sobrenome) {
		var decodedSobrenome = decodeHeader(sobrenome);
		return decodedSobrenome != null && !decodedSobrenome.isBlank()
			? decodedSobrenome.trim()
			: "Nao informado";
	}

	private LocalDate resolveDataNascimento(String dataNascimento) {
		try {
			var decodedDataNascimento = decodeHeader(dataNascimento);
			return decodedDataNascimento != null && !decodedDataNascimento.isBlank()
				? LocalDate.parse(decodedDataNascimento)
				: LocalDate.of(1900, 1, 1);
		} catch (RuntimeException ex) {
			return LocalDate.of(1900, 1, 1);
		}
	}

	private String decodeHeader(String value) {
		return value != null ? URLDecoder.decode(value, StandardCharsets.UTF_8) : null;
	}
}
