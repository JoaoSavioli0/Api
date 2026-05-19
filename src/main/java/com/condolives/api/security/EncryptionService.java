package com.condolives.api.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH_BIT = 128;

    private final SecretKey aesKey;

    public EncryptionService(@Value("${encryption.key}") String keyBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("encryption.key deve ser 256 bits (32 bytes em Base64)");
        }
        this.aesKey = new SecretKeySpec(keyBytes, "AES");
    }

    /** Criptografia com IV aleatório. Use para RG e telefone. */
    public String encrypt(String plaintext) {
        if (plaintext == null) return null;
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return doEncrypt(plaintext, iv);
    }

    /**
     * Criptografia com IV derivado via HMAC-SHA256.
     * Mesmo plaintext → mesmo ciphertext, preservando unicidade no BD.
     * Use para CPF.
     */
    public String encryptDeterministic(String plaintext) {
        if (plaintext == null) return null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(aesKey);
            byte[] hmac = mac.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] iv = Arrays.copyOf(hmac, IV_SIZE);
            return doEncrypt(plaintext, iv);
        } catch (Exception e) {
            throw new RuntimeException("Falha na criptografia determinística", e);
        }
    }

    public String decrypt(String encoded) {
        if (encoded == null) return null;
        try {
            byte[] data = Base64.getDecoder().decode(encoded);
            byte[] iv = Arrays.copyOfRange(data, 0, IV_SIZE);
            byte[] ciphertext = Arrays.copyOfRange(data, IV_SIZE, data.length);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Falha na descriptografia", e);
        }
    }

    private String doEncrypt(String plaintext, byte[] iv) {
        try {
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[IV_SIZE + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, IV_SIZE);
            System.arraycopy(ciphertext, 0, result, IV_SIZE, ciphertext.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException("Falha na criptografia", e);
        }
    }
}
