package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.booking.BookingResponse;
import com.condolives.api.dto.booking.CreateBookingRequest;

public interface BookingService {
    BookingResponse create(CreateBookingRequest request, UUID memberId, UUID condominiumId);
    List<BookingResponse> listAll(UUID condominiumId);
    List<BookingResponse> listMyBookings(UUID memberId, UUID condominiumId);
    void setPending(UUID id, UUID condominiumId);
    void setRejected(UUID id, UUID condominiumId);
}
