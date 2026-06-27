package com.educame.educame_api.presentation.controller;

import com.educame.educame_api.application.dto.disciplina.DisciplinaResumoResponse;
import com.educame.educame_api.application.usecase.disciplina.DisciplinaUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/disciplinas")
public class DisciplinaController {
	private final DisciplinaUseCase disciplinaUseCase;

	public DisciplinaController(DisciplinaUseCase disciplinaUseCase) {
		this.disciplinaUseCase = disciplinaUseCase;
	}

	@GetMapping
	public ResponseEntity<List<DisciplinaResumoResponse>> listarAtivas() {
		return ResponseEntity.ok(disciplinaUseCase.listarAtivas());
	}
}
