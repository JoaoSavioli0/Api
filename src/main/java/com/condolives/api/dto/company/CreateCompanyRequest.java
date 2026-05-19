package com.condolives.api.dto.company;

import jakarta.validation.constraints.NotBlank;

public record CreateCompanyRequest(
        @NotBlank String name,
        String cnpj,
        String phone,
        String email) {
}
