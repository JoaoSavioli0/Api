package com.condolives.api.dto.amenity;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.booking.BookingDetailResponse;
import com.condolives.api.entity.Amenity.Amenity;
import com.condolives.api.entity.Amenity.AmenityException;
import com.condolives.api.entity.Amenity.AmenitySchedule;
import com.condolives.api.entity.Amenity.Booking;

public record AmenityDetailResponseAdmin(
        UUID id,
        String name,
        int maxCapacity,
        int maxBookingDuration,
        String description,
        String observation,
        boolean active,
        List<ScheduleResponse> schedules,
        List<ExceptionResponse> upcomingExceptions,
        List<BookingDetailResponse> upcomingBookings) {

    public static AmenityDetailResponseAdmin from(
            Amenity a,
            List<AmenitySchedule> schedules,
            List<AmenityException> exceptions,
            List<Booking> bookings) {
        return new AmenityDetailResponseAdmin(
                a.getId(),
                a.getName(),
                a.getMaxCapacity(),
                a.getMaxBookingDuration(),
                a.getDescription(),
                a.getObservation(),
                a.getActive(),
                schedules.stream().map(ScheduleResponse::from).toList(),
                exceptions.stream().map(ExceptionResponse::from).toList(),
                bookings.stream().map(BookingDetailResponse::from).toList());
    }
}
