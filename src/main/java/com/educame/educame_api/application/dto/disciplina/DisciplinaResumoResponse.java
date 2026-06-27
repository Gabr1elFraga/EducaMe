package com.educame.educame_api.application.dto.disciplina;

import java.util.UUID;

public record DisciplinaResumoResponse(
	UUID id,
	String nome,
	String descricao
) {
}
