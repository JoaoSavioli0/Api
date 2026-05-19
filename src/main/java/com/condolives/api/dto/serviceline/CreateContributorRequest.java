package com.condolives.api.dto.serviceline;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record CreateContributorRequest(
        @NotBlank String sourceType,
        UUID staffId,
        UUID companyId,
        String name,
        String role) {
}
