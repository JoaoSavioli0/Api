package com.condolives.api.dto.outer;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;

public record CreateVisitorRequest(
        @NotBlank String name,
        String document,
        Instant expectedAt,
        String notes
) {}
