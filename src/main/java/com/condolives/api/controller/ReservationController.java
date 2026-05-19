package com.condolives.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.booking.BookingResponse;
import com.condolives.api.dto.booking.CreateBookingRequest;
import com.condolives.api.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> listAll(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(bookingService.listAll(condominiumId));
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(
            @RequestBody @Valid CreateBookingRequest request,
            Authentication authentication) {

        UUID residentId = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.create(request, residentId, condominiumId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> myBookings(Authentication authentication) {
        UUID residentId = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(bookingService.listMyBookings(residentId, condominiumId));
    }

    @PatchMapping("/{id}/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setPending(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        bookingService.setPending(id, condominiumId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setRejected(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        bookingService.setRejected(id, condominiumId);

        return ResponseEntity.noContent().build();
    }
}
