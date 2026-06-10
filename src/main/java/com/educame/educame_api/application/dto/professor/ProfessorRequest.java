package com.educame.educame_api.application.dto.professor;

import java.util.UUID;

public record ProfessorRequest(
	UUID authUserId,
	String nome,
	String sobrenome,
	String bio,
	UUID enderecoId,
	boolean ativo
) {
}
