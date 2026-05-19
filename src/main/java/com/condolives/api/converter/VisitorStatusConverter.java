package com.condolives.api.converter;

import com.condolives.api.enums.VisitorStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class VisitorStatusConverter implements AttributeConverter<VisitorStatus, String> {

    @Override
    public String convertToDatabaseColumn(VisitorStatus status) {
        return status == null ? null : status.toDbValue();
    }

    @Override
    public VisitorStatus convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : VisitorStatus.fromDbValue(dbValue);
    }
}
