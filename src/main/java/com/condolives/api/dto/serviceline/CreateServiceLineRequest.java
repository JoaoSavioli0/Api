package com.condolives.api.dto.serviceline;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateServiceLineRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String responsibleName,
        @NotNull LocalDate startDate,
        String estimatedEndDate,
        String estimatedCost,
        UUID linkedRequestId) {
}
