package com.educame.educame_api.infrastructure.persistence.jpa.converter;

import com.educame.educame_api.domain.enums.DisponibilidadeStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DisponibilidadeStatusConverter implements AttributeConverter<DisponibilidadeStatus, String> {
	@Override
	public String convertToDatabaseColumn(DisponibilidadeStatus attribute) {
		return attribute != null ? attribute.name().toLowerCase() : null;
	}

	@Override
	public DisponibilidadeStatus convertToEntityAttribute(String dbData) {
		return dbData != null && !dbData.isBlank()
			? DisponibilidadeStatus.valueOf(dbData.trim().toUpperCase())
			: null;
	}
}
