package com.educame.educame_api.application.dto.anuncio;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AnuncioAulaResponse(
	UUID id,
	UUID professorId,
	UUID disciplinaId,
	String disciplinaNome,
	String titulo,
	String descricao,
	BigDecimal valorHora,
	String modalidade,
	boolean ativo,
	List<DisponibilidadeResponse> disponibilidades
) {
	public record DisponibilidadeResponse(
		UUID id,
		OffsetDateTime inicio,
		OffsetDateTime fim,
		String status,
		String observacao
	) {
	}
}
