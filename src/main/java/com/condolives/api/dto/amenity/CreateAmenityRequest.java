package com.condolives.api.dto.amenity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateAmenityRequest(
        @NotBlank String name,
        @NotNull @Positive Integer maxCapacity,
        @NotNull @Positive Integer maxBookingDuration,
        String description,
        String observation) {
}
