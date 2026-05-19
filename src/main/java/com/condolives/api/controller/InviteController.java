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
import com.condolives.api.dto.auth.LoginResponse;
import com.condolives.api.dto.invite.ClaimInviteRequest;
import com.condolives.api.dto.invite.CreateInviteRequest;
import com.condolives.api.dto.invite.InvitePreviewResponse;
import com.condolives.api.dto.invite.InviteResponse;
import com.condolives.api.service.InviteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/invite")
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InviteResponse> create(
            @Valid @RequestBody CreateInviteRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inviteService.createInvite(request, condominiumId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InviteResponse>> list(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(inviteService.listInvites(condominiumId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> revoke(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        inviteService.revokeInvite(id, condominiumId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/claim/{token}")
    public ResponseEntity<InvitePreviewResponse> preview(@PathVariable String token) {
        return ResponseEntity.ok(inviteService.getPreview(token));
    }

    @PostMapping("/claim/{token}")
    public ResponseEntity<LoginResponse> claim(
            @PathVariable String token,
            @Valid @RequestBody ClaimInviteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inviteService.claimInvite(token, request));
    }
}
