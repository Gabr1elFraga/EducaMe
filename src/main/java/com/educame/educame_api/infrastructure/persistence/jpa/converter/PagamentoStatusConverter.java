package com.educame.educame_api.infrastructure.persistence.jpa.converter;

import com.educame.educame_api.domain.enums.PagamentoStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PagamentoStatusConverter implements AttributeConverter<PagamentoStatus, String> {
	@Override
	public String convertToDatabaseColumn(PagamentoStatus attribute) {
		if (attribute == null) {
			return null;
		}
		return switch (attribute) {
			case PENDENTE -> "pendente";
			case APROVADO -> "aprovado";
			case RECUSADO -> "recusado";
			case REEMBOLSADO -> "reembolsado";
			case CANCELADO -> "cancelado";
		};
	}

	@Override
	public PagamentoStatus convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isBlank()) {
			return null;
		}
		return switch (dbData.trim().toLowerCase()) {
			case "pendente" -> PagamentoStatus.PENDENTE;
			case "aprovado" -> PagamentoStatus.APROVADO;
			case "recusado" -> PagamentoStatus.RECUSADO;
			case "reembolsado", "estornado" -> PagamentoStatus.REEMBOLSADO;
			case "cancelado" -> PagamentoStatus.CANCELADO;
			default -> PagamentoStatus.valueOf(dbData.trim().toUpperCase());
		};
	}
}
