package com.condolives.api.converter;

import com.condolives.api.enums.DeliveryStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DeliveryStatusConverter implements AttributeConverter<DeliveryStatus, String> {

    @Override
    public String convertToDatabaseColumn(DeliveryStatus status) {
        return status == null ? null : status.toDbValue();
    }

    @Override
    public DeliveryStatus convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : DeliveryStatus.fromDbValue(dbValue);
    }
}
