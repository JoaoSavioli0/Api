package com.condolives.api.converter;

import com.condolives.api.enums.TradeStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TradeStatusConverter implements AttributeConverter<TradeStatus, String> {

    @Override
    public String convertToDatabaseColumn(TradeStatus attribute) {
        return attribute == null ? null : attribute.toDbValue();
    }

    @Override
    public TradeStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TradeStatus.fromDbValue(dbData);
    }
}
