package com.condolives.api.dto.unit;

import jakarta.validation.constraints.NotBlank;

public record CreateUnitRequest(
        @NotBlank String identifier,
        String block,
        String street,
        Integer floor,
        @NotBlank String type) {
}
