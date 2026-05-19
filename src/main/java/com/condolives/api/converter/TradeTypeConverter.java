package com.condolives.api.converter;

import com.condolives.api.enums.TradeType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TradeTypeConverter implements AttributeConverter<TradeType, String> {

    @Override
    public String convertToDatabaseColumn(TradeType attribute) {
        return attribute == null ? null : attribute.toDbValue();
    }

    @Override
    public TradeType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TradeType.fromDbValue(dbData);
    }
}
