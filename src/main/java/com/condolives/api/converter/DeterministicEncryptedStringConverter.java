package com.condolives.api.converter;

import com.condolives.api.security.EncryptionServiceHolder;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converte String para/de AES-256-GCM com IV derivado via HMAC.
 * Mesmo plaintext sempre produz o mesmo ciphertext, preservando
 * a constraint UNIQUE do CPF no banco. Use apenas para CPF.
 */
@Converter
public class DeterministicEncryptedStringConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return EncryptionServiceHolder.get().encryptDeterministic(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return EncryptionServiceHolder.get().decrypt(dbData);
    }
}
