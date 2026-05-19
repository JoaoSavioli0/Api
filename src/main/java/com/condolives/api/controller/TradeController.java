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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.post.Trade.CreateTradeRequest;
import com.condolives.api.dto.post.Trade.TradeAdminDetailResponse;
import com.condolives.api.dto.post.Trade.TradeListResponse;
import com.condolives.api.dto.post.Trade.TradeResponse;
import com.condolives.api.service.TradeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping
    public ResponseEntity<TradeResponse> create(
            @Valid @RequestBody CreateTradeRequest request,
            Authentication authentication) {

        UUID residentId = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tradeService.createTrade(request, residentId, condominiumId));
    }

    @GetMapping
    public ResponseEntity<List<TradeListResponse>> list(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(tradeService.listTrades(condominiumId));
    }

    @GetMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TradeAdminDetailResponse> adminDetail(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(tradeService.getTradeAdmin(id, condominiumId));
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        UUID memberId = UUID.fromString((String) authentication.getPrincipal());
        boolean isAdmin = AuthHelper.isAdmin(authentication);

        tradeService.deleteTrade(id, memberId, condominiumId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @PathVariable String status,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        tradeService.updateStatus(id, status, condominiumId);

        return ResponseEntity.noContent().build();
    }
}
