package com.condolives.api.dto.booking;

import com.condolives.api.entity.Amenity.Booking;

public record BookingTimeResponse(
        String date,
        String startTime,
        String endTime,
        String status) {
    public static BookingTimeResponse from(Booking b) {
        return new BookingTimeResponse(
                b.getDate().toString(),
                b.getStartTime().toString(),
                b.getEndTime().toString(),
                b.getStatus().name());
    }
}
