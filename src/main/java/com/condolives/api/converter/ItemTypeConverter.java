package com.condolives.api.converter;

import com.condolives.api.enums.ItemType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ItemTypeConverter implements AttributeConverter<ItemType, String> {

    @Override
    public String convertToDatabaseColumn(ItemType attribute) {
        return attribute == null ? null : attribute.toDbValue();
    }

    @Override
    public ItemType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ItemType.fromDbValue(dbData);
    }
}
