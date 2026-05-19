package com.condolives.api.dto.amenity;

import java.util.UUID;

import com.condolives.api.entity.Amenity.Amenity;

public record AmenityResponse(
        UUID id,
        String name,
        int maxCapacity,
        int maxBookingDuration,
        String description,
        String observation,
        boolean active) {

    public static AmenityResponse from(Amenity a) {
        return new AmenityResponse(
                a.getId(),
                a.getName(),
                a.getMaxCapacity(),
                a.getMaxBookingDuration(),
                a.getDescription(),
                a.getObservation(),
                a.getActive());
    }
}
