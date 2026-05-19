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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.unit.CreateUnitRequest;
import com.condolives.api.dto.unit.UnitResponse;
import com.condolives.api.service.UnitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/unit")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnitResponse> create(
            @Valid @RequestBody CreateUnitRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(unitService.createUnit(request, condominiumId));
    }

    @GetMapping
    public ResponseEntity<List<UnitResponse>> list(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(unitService.listUnits(condominiumId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        unitService.deleteUnit(id, condominiumId);
        return ResponseEntity.noContent().build();
    }
}
