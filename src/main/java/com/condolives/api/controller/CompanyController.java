package com.condolives.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.company.CompanyResponse;
import com.condolives.api.dto.company.CreateCompanyRequest;
import com.condolives.api.service.CompanyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SYNDIC')")
    public ResponseEntity<List<CompanyResponse>> list(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(companyService.list(condominiumId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYNDIC')")
    public ResponseEntity<CompanyResponse> getById(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(companyService.getById(id, condominiumId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponse> create(
            @RequestBody @Valid CreateCompanyRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.create(request, condominiumId));
    }
}
