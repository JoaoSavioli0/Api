package com.condolives.api.dto.booking;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.condolives.api.entity.Amenity.Amenity;
import com.condolives.api.entity.Amenity.Booking;
import com.condolives.api.enums.BookingStatus;

public record BookingDetailResponse(
        UUID id,
        UUID amenityId,
        String amenityName,
        UUID memberId,
        String memberName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Integer guestCount,
        BookingStatus status,
        String statusDescricao,
        Instant createdAt) {
    public static BookingDetailResponse from(Booking b) {
        return new BookingDetailResponse(
                b.getId(),
                b.getAmenity().getId(),
                b.getAmenity().getName(),
                b.getMember().getId(),
                b.getMember().getUser().getName(),
                b.getDate(),
                b.getStartTime(),
                b.getEndTime(),
                b.getGuestCount(),
                b.getStatus(),
                b.getStatus().getDescricao(),
                b.getCreatedAt());
    }
}
