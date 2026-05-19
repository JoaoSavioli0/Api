package com.condolives.api.dto.auth;

import com.condolives.api.validation.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @StrongPassword String password,
        String cpf,
        String phone) {
}
