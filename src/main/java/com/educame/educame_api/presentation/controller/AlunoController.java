package com.educame.educame_api.presentation.controller;

import com.educame.educame_api.application.dto.aluno.AlunoRequest;
import com.educame.educame_api.application.dto.aluno.AlunoResponse;
import com.educame.educame_api.application.usecase.aluno.AlunoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alunos")
public class AlunoController {

	private final AlunoUseCase alunoUseCase;

	public AlunoController(AlunoUseCase alunoUseCase) {
		this.alunoUseCase = alunoUseCase;
	}

	@GetMapping
	public List<AlunoResponse> list() {
		return alunoUseCase.list();
	}

	@GetMapping("/{id}")
	public AlunoResponse findById(@PathVariable UUID id) {
		return alunoUseCase.findById(id);
	}

	@PostMapping
	public ResponseEntity<AlunoResponse> create(@RequestBody AlunoRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(alunoUseCase.create(request));
	}

	@PutMapping("/{id}")
	public AlunoResponse update(@PathVariable UUID id, @RequestBody AlunoRequest request) {
		return alunoUseCase.update(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		alunoUseCase.delete(id);
		return ResponseEntity.noContent().build();
	}
}
