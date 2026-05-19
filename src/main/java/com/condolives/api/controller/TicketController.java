package com.condolives.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.post.Ticket.CreateTicketRequest;
import com.condolives.api.dto.post.Ticket.RawTicketResponse;
import com.condolives.api.dto.post.Ticket.TicketDetailResponse;
import com.condolives.api.dto.post.Ticket.TicketDetailResponseAdmin;
import com.condolives.api.dto.post.Ticket.TicketResponse;
import com.condolives.api.service.TicketService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RawTicketResponse> create(
            @ModelAttribute @Valid CreateTicketRequest request,
            @RequestParam(value = "showName", required = false, defaultValue = "true") boolean showName,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {

        request.setShowName(showName);
        UUID residentId = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ticketService.createTicket(request, images, residentId, condominiumId));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> list(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(ticketService.listTickets(condominiumId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailResponse> detail(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(ticketService.getTicket(id, condominiumId));
    }

    @GetMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketDetailResponseAdmin> adminDetail(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(ticketService.getTicketAdmin(id, condominiumId));
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        UUID residentId = UUID.fromString((String) authentication.getPrincipal());
        boolean isAdmin = AuthHelper.isAdmin(authentication);

        ticketService.deleteTicket(id, residentId, condominiumId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketDetailResponseAdmin> updateStatus(
            @PathVariable UUID id,
            @PathVariable String status,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(ticketService.updateStatus(id, status, condominiumId));
    }
}
