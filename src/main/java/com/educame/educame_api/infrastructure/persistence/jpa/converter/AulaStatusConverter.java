package com.educame.educame_api.infrastructure.persistence.jpa.converter;

import com.educame.educame_api.domain.enums.AulaStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AulaStatusConverter implements AttributeConverter<AulaStatus, String> {
	@Override
	public String convertToDatabaseColumn(AulaStatus attribute) {
		return attribute != null ? attribute.name().toLowerCase() : null;
	}

	@Override
	public AulaStatus convertToEntityAttribute(String dbData) {
		return dbData != null && !dbData.isBlank()
			? AulaStatus.valueOf(dbData.trim().toUpperCase())
			: null;
	}
}
