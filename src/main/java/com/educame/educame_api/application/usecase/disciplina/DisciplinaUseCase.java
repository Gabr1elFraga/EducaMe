package com.educame.educame_api.application.usecase.disciplina;

import com.educame.educame_api.application.dto.disciplina.DisciplinaResumoResponse;
import com.educame.educame_api.infrastructure.persistence.jpa.entity.DisciplinaEntity;
import com.educame.educame_api.infrastructure.persistence.jpa.repository.DisciplinaJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisciplinaUseCase {
	private final DisciplinaJpaRepository disciplinaRepository;

	public DisciplinaUseCase(DisciplinaJpaRepository disciplinaRepository) {
		this.disciplinaRepository = disciplinaRepository;
	}

	public List<DisciplinaResumoResponse> listarAtivas() {
		return disciplinaRepository.findByAtivoTrueOrderByNomeAsc()
			.stream()
			.map(this::toResponse)
			.toList();
	}

	private DisciplinaResumoResponse toResponse(DisciplinaEntity disciplina) {
		return new DisciplinaResumoResponse(
			disciplina.getId(),
			disciplina.getNome(),
			disciplina.getDescricao()
		);
	}
}
