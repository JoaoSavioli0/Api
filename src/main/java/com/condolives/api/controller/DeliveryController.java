package com.condolives.api.controller;

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
import com.condolives.api.dto.outer.DeliveryResponse;
import com.condolives.api.dto.outer.PickupDeliveryRequest;
import com.condolives.api.service.GatehouseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/deliveries")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class DeliveryController {

    private final GatehouseService gatehouseService;

    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> list(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(gatehouseService.listDeliveries(condominiumId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DeliveryResponse> register(
            @RequestParam String sender,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String notes,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            Authentication authentication) {
        UUID receivedBy = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gatehouseService.registerDelivery(sender, description, notes, photo, receivedBy, condominiumId));
    }

    @PatchMapping("/{id}/pickup")
    public ResponseEntity<DeliveryResponse> pickup(
            @PathVariable UUID id,
            @Valid @RequestBody PickupDeliveryRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(gatehouseService.pickupDelivery(id, request.pickedUpBy(), condominiumId));
    }
}
