package com.condolives.api.dto.auth;

import java.util.UUID;

import com.condolives.api.validation.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
                @NotNull UUID condominiumId,
                @NotBlank String name,
                @NotBlank @Email String email,
                @StrongPassword String password,
                @NotBlank @Pattern(regexp = "\\d{11}") String cpf,
                String rg,
                String phone,
                UUID unitId,
                UUID guardianId) {
}
