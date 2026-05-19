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
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.notice.CreateNoticeRequest;
import com.condolives.api.dto.notice.NoticeResponse;
import com.condolives.api.dto.notice.UnreadNoticeResponse;
import com.condolives.api.service.NoticeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NoticeResponse>> list(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(noticeService.list(condominiumId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoticeResponse> create(
            @RequestBody @Valid CreateNoticeRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        UUID memberId = UUID.fromString((String) authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticeService.create(request, condominiumId, memberId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoticeResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid CreateNoticeRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(noticeService.update(id, request, condominiumId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        noticeService.delete(id, condominiumId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/target-options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> targetOptions(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(noticeService.getTargetOptions(condominiumId));
    }

    // Resident endpoints
    @GetMapping("/unread")
    public ResponseEntity<List<UnreadNoticeResponse>> unread(Authentication authentication) {
        UUID memberId = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(noticeService.listUnread(memberId, condominiumId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<UnreadNoticeResponse>> myNotices(Authentication authentication) {
        UUID memberId = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(noticeService.listAllForMember(memberId, condominiumId));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID memberId = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        noticeService.markAsRead(id, memberId, condominiumId);
        return ResponseEntity.noContent().build();
    }
}
