package com.condolives.api.dto.amenity;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.booking.BookingTimeResponse;
import com.condolives.api.entity.Amenity.Amenity;
import com.condolives.api.entity.Amenity.AmenityException;
import com.condolives.api.entity.Amenity.AmenitySchedule;
import com.condolives.api.entity.Amenity.Booking;

public record AmenityDetailResponse(
        UUID id,
        String name,
        int maxCapacity,
        int maxBookingDuration,
        String description,
        String observation,
        boolean active,
        List<ScheduleResponse> schedules,
        List<ExceptionResponse> upcomingExceptions,
        List<BookingTimeResponse> upcomingBookings) {

    public static AmenityDetailResponse from(
            Amenity a,
            List<AmenitySchedule> schedules,
            List<AmenityException> exceptions,
            List<Booking> bookings) {
        return new AmenityDetailResponse(
                a.getId(),
                a.getName(),
                a.getMaxCapacity(),
                a.getMaxBookingDuration(),
                a.getDescription(),
                a.getObservation(),
                a.getActive(),
                schedules.stream().map(ScheduleResponse::from).toList(),
                exceptions.stream().map(ExceptionResponse::from).toList(),
                bookings.stream().map(BookingTimeResponse::from).toList());
    }
}
