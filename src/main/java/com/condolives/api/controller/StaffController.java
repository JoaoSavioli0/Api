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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.staff.CreateStaffRequest;
import com.condolives.api.dto.staff.StaffResponse;
import com.condolives.api.dto.staff.UpdateStaffRequest;
import com.condolives.api.enums.StaffCategory;
import com.condolives.api.service.StaffService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SYNDIC')")
    public ResponseEntity<List<StaffResponse>> list(
            Authentication authentication,
            @RequestParam(required = false) StaffCategory category) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(staffService.list(condominiumId, category));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYNDIC')")
    public ResponseEntity<StaffResponse> getById(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(staffService.getById(id, condominiumId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaffResponse> create(
            @RequestBody @Valid CreateStaffRequest request,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(staffService.create(request, condominiumId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaffResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateStaffRequest request,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(staffService.update(id, request, condominiumId));
    }

    @PatchMapping("/{id}/dismiss")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> dismiss(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID condominiumId = AuthHelper.condominiumId(authentication);
        staffService.dismiss(id, condominiumId);
        return ResponseEntity.noContent().build();
    }
}
