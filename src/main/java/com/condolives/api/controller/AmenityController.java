package com.condolives.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.amenity.AmenityDetailResponse;
import com.condolives.api.dto.amenity.AmenityDetailResponseAdmin;
import com.condolives.api.dto.amenity.AmenityResponse;
import com.condolives.api.dto.amenity.CreateAmenityRequest;
import com.condolives.api.dto.amenity.CreateExceptionRequest;
import com.condolives.api.dto.amenity.ExceptionResponse;
import com.condolives.api.dto.amenity.ScheduleResponse;
import com.condolives.api.dto.amenity.SetScheduleRequest;
import com.condolives.api.dto.amenity.UpdateAmenityRequest;
import com.condolives.api.service.AmenityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @PostMapping
    public ResponseEntity<AmenityResponse> create(
            @RequestBody @Valid CreateAmenityRequest request,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(amenityService.create(request, condominiumId));
    }

    @GetMapping
    public ResponseEntity<List<AmenityResponse>> list(
            @RequestParam(defaultValue = "false") boolean activeOnly,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(amenityService.list(condominiumId, activeOnly));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityDetailResponse> getDetail(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(amenityService.getDetail(id, condominiumId));
    }

    @GetMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AmenityDetailResponseAdmin> getDetailAdmin(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(amenityService.getDetailAdmin(id, condominiumId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AmenityResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateAmenityRequest request,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(amenityService.update(id, request, condominiumId));
    }

    @PutMapping("/{id}/schedules/{dayOfWeek}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleResponse> setSchedule(
            @PathVariable UUID id,
            @PathVariable short dayOfWeek,
            @RequestBody @Valid SetScheduleRequest request,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(amenityService.setSchedule(id, dayOfWeek, request, condominiumId));
    }

    // @DeleteMapping("/{id}/schedules/{dayOfWeek}")
    // public ResponseEntity<Void> deleteSchedule(
    // @PathVariable UUID id,
    // @PathVariable short dayOfWeek,
    // Authentication authentication) {

    // UUID condominiumId = AuthHelper.condominiumId(authentication);
    // amenityService.deleteSchedule(id, dayOfWeek, condominiumId);
    // return ResponseEntity.noContent().build();
    // }

    @PostMapping("/{id}/exceptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExceptionResponse> addException(
            @PathVariable UUID id,
            @RequestBody @Valid CreateExceptionRequest request,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(amenityService.addException(id, request, condominiumId));
    }

    @DeleteMapping("/exceptions/{exceptionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteException(
            @PathVariable UUID exceptionId,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        amenityService.deleteException(exceptionId, condominiumId);
        return ResponseEntity.noContent().build();
    }
}
