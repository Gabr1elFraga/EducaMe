package com.educame.educame_api.infrastructure.persistence.jpa.converter;

import com.educame.educame_api.domain.enums.GeneroTipo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GeneroTipoConverter implements AttributeConverter<GeneroTipo, String> {
	@Override
	public String convertToDatabaseColumn(GeneroTipo attribute) {
		return attribute != null ? attribute.name() : null;
	}

	@Override
	public GeneroTipo convertToEntityAttribute(String dbData) {
		return dbData != null && !dbData.isBlank()
			? GeneroTipo.valueOf(dbData.trim().toUpperCase())
			: GeneroTipo.NAO_INFORMADO;
	}
}
