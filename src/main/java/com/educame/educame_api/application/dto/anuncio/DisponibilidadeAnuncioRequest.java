package com.educame.educame_api.application.dto.anuncio;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record DisponibilidadeAnuncioRequest(
	@NotNull OffsetDateTime inicio,
	@NotNull OffsetDateTime fim,
	@Size(max = 1000) String observacao
) {
}
