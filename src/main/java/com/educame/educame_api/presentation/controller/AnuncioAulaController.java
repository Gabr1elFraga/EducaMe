package com.educame.educame_api.presentation.controller;

import com.educame.educame_api.application.dto.anuncio.AnuncioAulaRequest;
import com.educame.educame_api.application.dto.anuncio.AnuncioAulaResponse;
import com.educame.educame_api.application.dto.anuncio.AtualizarStatusAnuncioRequest;
import com.educame.educame_api.application.dto.anuncio.DisponibilidadeAnuncioRequest;
import com.educame.educame_api.application.usecase.anuncio.AnuncioAulaUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/anuncios")
public class AnuncioAulaController {
	private final AnuncioAulaUseCase anuncioAulaUseCase;

	public AnuncioAulaController(AnuncioAulaUseCase anuncioAulaUseCase) {
		this.anuncioAulaUseCase = anuncioAulaUseCase;
	}

	@GetMapping("/me")
	public ResponseEntity<List<AnuncioAulaResponse>> listarMeusAnuncios(
		@AuthenticationPrincipal Jwt jwt,
		@RequestHeader(value = "X-Auth-User-Id", required = false) UUID authUserId
	) {
		return ResponseEntity.ok(anuncioAulaUseCase.listarMeusAnuncios(resolveAuthUserId(jwt, authUserId)));
	}

	@PostMapping
	public ResponseEntity<AnuncioAulaResponse> criar(
		@AuthenticationPrincipal Jwt jwt,
		@RequestHeader(value = "X-Auth-User-Id", required = false) UUID authUserId,
		@Valid @RequestBody AnuncioAulaRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(anuncioAulaUseCase.criar(resolveAuthUserId(jwt, authUserId), request));
	}

	@PutMapping("/{anuncioId}")
	public ResponseEntity<AnuncioAulaResponse> atualizar(
		@AuthenticationPrincipal Jwt jwt,
		@RequestHeader(value = "X-Auth-User-Id", required = false) UUID authUserId,
		@PathVariable UUID anuncioId,
		@Valid @RequestBody AnuncioAulaRequest request
	) {
		return ResponseEntity.ok(anuncioAulaUseCase.atualizar(resolveAuthUserId(jwt, authUserId), anuncioId, request));
	}

	@PatchMapping("/{anuncioId}/status")
	public ResponseEntity<AnuncioAulaResponse> atualizarStatus(
		@AuthenticationPrincipal Jwt jwt,
		@RequestHeader(value = "X-Auth-User-Id", required = false) UUID authUserId,
		@PathVariable UUID anuncioId,
		@RequestBody AtualizarStatusAnuncioRequest request
	) {
		return ResponseEntity.ok(anuncioAulaUseCase.atualizarStatus(resolveAuthUserId(jwt, authUserId), anuncioId, request));
	}

	@PostMapping("/{anuncioId}/disponibilidades")
	public ResponseEntity<AnuncioAulaResponse> adicionarDisponibilidade(
		@AuthenticationPrincipal Jwt jwt,
		@RequestHeader(value = "X-Auth-User-Id", required = false) UUID authUserId,
		@PathVariable UUID anuncioId,
		@Valid @RequestBody DisponibilidadeAnuncioRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(anuncioAulaUseCase.adicionarDisponibilidade(resolveAuthUserId(jwt, authUserId), anuncioId, request));
	}

	@DeleteMapping("/{anuncioId}/disponibilidades/{disponibilidadeId}")
	public ResponseEntity<AnuncioAulaResponse> removerDisponibilidade(
		@AuthenticationPrincipal Jwt jwt,
		@RequestHeader(value = "X-Auth-User-Id", required = false) UUID authUserId,
		@PathVariable UUID anuncioId,
		@PathVariable UUID disponibilidadeId
	) {
		return ResponseEntity.ok(anuncioAulaUseCase.removerDisponibilidade(
			resolveAuthUserId(jwt, authUserId),
			anuncioId,
			disponibilidadeId
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
}
