package com.educame.educame_api.application.dto.anuncio;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record AnuncioAulaRequest(
	@NotNull UUID disciplinaId,
	@NotBlank @Size(max = 160) String titulo,
	@Size(max = 4000) String descricao,
	@NotNull @DecimalMin("0.00") BigDecimal valorHora,
	@NotBlank @Size(max = 40) String modalidade,
	boolean ativo
) {
}
