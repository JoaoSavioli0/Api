package com.condolives.api.converter;

import com.condolives.api.enums.Importance;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ImportanceConverter implements AttributeConverter<Importance, String> {

    @Override
    public String convertToDatabaseColumn(Importance attribute) {
        return attribute == null ? null : attribute.toDbValue();
    }

    @Override
    public Importance convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Importance.fromDbValue(dbData);
    }
}
