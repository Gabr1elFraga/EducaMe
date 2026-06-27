package com.educame.educame_api.presentation.controller;

import com.educame.educame_api.application.dto.perfil.AtualizarPerfilUsuarioRequest;
import com.educame.educame_api.application.dto.perfil.PerfilUsuarioResponse;
import com.educame.educame_api.application.usecase.perfil.PerfilUsuarioUseCase;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/perfil")
public class PerfilUsuarioController {
	private final PerfilUsuarioUseCase perfilUsuarioUseCase;

	public PerfilUsuarioController(PerfilUsuarioUseCase perfilUsuarioUseCase) {
		this.perfilUsuarioUseCase = perfilUsuarioUseCase;
	}

	@GetMapping("/me")
	public ResponseEntity<PerfilUsuarioResponse> buscarMeuPerfil(
		@AuthenticationPrincipal Jwt jwt,
		@RequestHeader(value = "X-Auth-User-Id", required = false) UUID authUserId
	) {
		return ResponseEntity.ok(perfilUsuarioUseCase.buscarMeuPerfil(resolveAuthUserId(jwt, authUserId)));
	}

	@PutMapping("/me")
	public ResponseEntity<PerfilUsuarioResponse> atualizarMeuPerfil(
		@AuthenticationPrincipal Jwt jwt,
		@RequestHeader(value = "X-Auth-User-Id", required = false) UUID authUserId,
		@Valid @RequestBody AtualizarPerfilUsuarioRequest request
	) {
		return ResponseEntity.ok(perfilUsuarioUseCase.atualizarMeuPerfil(resolveAuthUserId(jwt, authUserId), request));
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
