package com.condolives.api.converter;

import com.condolives.api.enums.StaffCategory;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StaffCategoryConverter implements AttributeConverter<StaffCategory, String> {

    @Override
    public String convertToDatabaseColumn(StaffCategory category) {
        return category == null ? null : category.toDbValue();
    }

    @Override
    public StaffCategory convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : StaffCategory.fromDbValue(dbValue);
    }
}
