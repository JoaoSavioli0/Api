package com.condolives.api.security;

import org.springframework.stereotype.Component;

/**
 * Bridge estático que expõe o EncryptionService para os JPA AttributeConverters,
 * os quais não são gerenciados pelo Spring e não suportam injeção direta.
 */
@Component
public class EncryptionServiceHolder {

    private static EncryptionService instance;

    public EncryptionServiceHolder(EncryptionService service) {
        EncryptionServiceHolder.instance = service;
    }

    public static EncryptionService get() {
        if (instance == null) {
            throw new IllegalStateException("EncryptionService ainda não foi inicializado pelo Spring");
        }
        return instance;
    }
}
