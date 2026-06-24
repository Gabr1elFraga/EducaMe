package com.educame.educame_api.application.dto.professor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AtualizarProfessorPerfilRequest(
	@Size(max = 4000, message = "A bio deve ter no maximo 4000 caracteres.")
	String bio,
	@NotNull(message = "O campo ativo e obrigatorio.")
	Boolean ativo,
	@Size(max = 1000, message = "O diploma deve ter no maximo 1000 caracteres.")
	String diploma,
	@PositiveOrZero(message = "O valor da hora aula deve ser maior ou igual a zero.")
	BigDecimal valorHoraAula
) {
}
