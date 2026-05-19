package com.condolives.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.booking.BookingResponse;
import com.condolives.api.dto.member.MemberDetailResponseAdmin;
import com.condolives.api.dto.member.MemberListResponse;
import com.condolives.api.dto.member.MemberResponse;
import com.condolives.api.dto.member.UpdateMemberRequest;
import com.condolives.api.service.BookingService;
import com.condolives.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> me(Authentication authentication) {
        UUID memberId = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(userService.getMember(memberId, condominiumId));
    }

    @GetMapping
    public ResponseEntity<List<MemberListResponse>> list(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(userService.listMembers(condominiumId));
    }

    @GetMapping("/collaborators")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MemberListResponse>> listCollaborators(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(userService.listCollaborators(condominiumId));
    }

    @GetMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDetailResponseAdmin> getMemberAdmin(Authentication authentication,
            @PathVariable UUID id) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(userService.getMemberAdmin(id, condominiumId));
    }

    @GetMapping("/{id}/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getMemberBookings(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(bookingService.listMyBookings(id, condominiumId));
    }

    @PatchMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberResponse> updateProfileAvatar(Authentication authentication,
            @RequestPart("avatar") MultipartFile avatar) {
        UUID memberId = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(userService.updateProfile(memberId, condominiumId, avatar));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberListResponse> updateMember(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateMemberRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(userService.updateMember(id, condominiumId, request));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateMember(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        userService.deactivateMember(id, condominiumId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateMember(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        userService.activateMember(id, condominiumId);
        return ResponseEntity.noContent().build();
    }
}
