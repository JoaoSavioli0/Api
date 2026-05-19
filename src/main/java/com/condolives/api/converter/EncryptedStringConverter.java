package com.condolives.api.converter;

import com.condolives.api.security.EncryptionServiceHolder;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Converte String para/de AES-256-GCM com IV aleatório. Use para RG e telefone. */
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return EncryptionServiceHolder.get().encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return EncryptionServiceHolder.get().decrypt(dbData);
    }
}
