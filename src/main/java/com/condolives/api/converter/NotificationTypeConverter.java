package com.condolives.api.converter;

import com.condolives.api.enums.NotificationType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NotificationTypeConverter implements AttributeConverter<NotificationType, String> {

    @Override
    public String convertToDatabaseColumn(NotificationType type) {
        return type == null ? null : type.toDbValue();
    }

    @Override
    public NotificationType convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : NotificationType.fromDbValue(dbValue);
    }
}
