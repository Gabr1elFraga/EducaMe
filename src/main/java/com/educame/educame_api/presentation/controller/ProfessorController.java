package com.educame.educame_api.presentation.controller;

import com.educame.educame_api.application.dto.professor.ProfessorRequest;
import com.educame.educame_api.application.dto.professor.ProfessorResponse;
import com.educame.educame_api.application.usecase.professor.ProfessorUseCase;
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
@RequestMapping("/api/v1/professores")
public class ProfessorController {
	private final ProfessorUseCase professorUseCase;

	public ProfessorController(ProfessorUseCase professorUseCase) {
		this.professorUseCase = professorUseCase;
	}

	@GetMapping
	public List<ProfessorResponse> list() {
		return professorUseCase.list();
	}

	@GetMapping("/{id}")
	public ProfessorResponse findById(@PathVariable UUID id) {
		return professorUseCase.findById(id);
	}

	@PostMapping
	public ResponseEntity<ProfessorResponse> create(@RequestBody ProfessorRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(professorUseCase.create(request));
	}

	@PutMapping("/{id}")
	public ProfessorResponse update(@PathVariable UUID id, @RequestBody ProfessorRequest request) {
		return professorUseCase.update(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		professorUseCase.delete(id);
		return ResponseEntity.noContent().build();
	}
}
