package com.condolives.api.converter;

import com.condolives.api.enums.UnitType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UnitTypeConverter implements AttributeConverter<UnitType, String> {

    @Override
    public String convertToDatabaseColumn(UnitType type) {
        return type == null ? null : type.toDbValue();
    }

    @Override
    public UnitType convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : UnitType.fromDbValue(dbValue);
    }
}
