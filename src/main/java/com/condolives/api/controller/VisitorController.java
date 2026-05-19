package com.condolives.api.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.outer.VisitorArrivalRequest;
import com.condolives.api.dto.outer.VisitorResponse;
import com.condolives.api.service.GatehouseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/visitors")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class VisitorController {

    private final GatehouseService gatehouseService;

    @GetMapping
    public ResponseEntity<List<VisitorResponse>> list(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(gatehouseService.listVisitors(condominiumId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VisitorResponse> register(
            @RequestParam String name,
            @RequestParam(required = false) String document,
            @RequestParam(required = false) String expectedAt,
            @RequestParam(required = false) String notes,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestParam UUID memberId,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        Instant expectedAtInstant = expectedAt != null ? Instant.parse(expectedAt) : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gatehouseService.registerVisitor(name, document, expectedAtInstant, notes, photo, memberId, condominiumId));
    }

    @PatchMapping("/{id}/arrive")
    public ResponseEntity<VisitorResponse> arrive(
            @PathVariable UUID id,
            @Valid @RequestBody VisitorArrivalRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(gatehouseService.recordArrival(id, request, condominiumId));
    }

    @PatchMapping("/{id}/leave")
    public ResponseEntity<VisitorResponse> leave(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(gatehouseService.recordDeparture(id, condominiumId));
    }
}
