package com.educame.educame_api.presentation.controller;

import com.educame.educame_api.application.dto.aluno.AlunoResponse;
import com.educame.educame_api.application.dto.autenticacao.CadastroAlunoRequest;
import com.educame.educame_api.application.dto.autenticacao.CadastroProfessorRequest;
import com.educame.educame_api.application.dto.professor.ProfessorResponse;
import com.educame.educame_api.application.usecase.autenticacao.AutenticacaoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/autenticacao")
public class AutenticacaoController {
	private final AutenticacaoUseCase autenticacaoUseCase;

	public AutenticacaoController(AutenticacaoUseCase autenticacaoUseCase) {
		this.autenticacaoUseCase = autenticacaoUseCase;
	}

	@PostMapping("/alunos")
	public ResponseEntity<AlunoResponse> cadastrarAluno(
		@Valid @RequestBody CadastroAlunoRequest request,
		@AuthenticationPrincipal Jwt jwt
	) {
		ensureJwtMatchesRequest(jwt, request.authUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(autenticacaoUseCase.cadastrarAluno(request));
	}

	@PostMapping("/professores")
	public ResponseEntity<ProfessorResponse> cadastrarProfessor(
		@Valid @RequestBody CadastroProfessorRequest request,
		@AuthenticationPrincipal Jwt jwt
	) {
		ensureJwtMatchesRequest(jwt, request.authUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(autenticacaoUseCase.cadastrarProfessor(request));
	}

	private void ensureJwtMatchesRequest(Jwt jwt, UUID authUserId) {
		if (jwt == null || jwt.getSubject() == null || authUserId == null) {
			return;
		}

		if (!jwt.getSubject().equals(authUserId.toString())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "O token nao corresponde ao usuario informado.");
		}
	}
}
