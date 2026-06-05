package com.educame.educame_api.application.dto.aluno;

import com.educame.educame_api.domain.enums.GeneroTipo;

import java.time.LocalDate;
import java.util.UUID;

public record AlunoRequest(
	UUID authUserId,
	String nome,
	String sobrenome,
	LocalDate dataNascimento,
	GeneroTipo genero,
	UUID enderecoId
) {
}
