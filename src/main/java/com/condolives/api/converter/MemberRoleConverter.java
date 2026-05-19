package com.condolives.api.converter;

import com.condolives.api.enums.MemberRole;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MemberRoleConverter implements AttributeConverter<MemberRole, String> {

    @Override
    public String convertToDatabaseColumn(MemberRole role) {
        return role == null ? null : role.toDbValue();
    }

    @Override
    public MemberRole convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : MemberRole.fromDbValue(dbValue);
    }
}
