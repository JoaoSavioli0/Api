package com.condolives.api.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.insight.GenerateInsightRequest;
import com.condolives.api.dto.insight.InsightGenerationResponse;
import com.condolives.api.service.InsightService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/insights")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class InsightController {

    private final InsightService insightService;

    @GetMapping("/credits")
    public ResponseEntity<Map<String, Integer>> getCredits(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(Map.of("credits", insightService.getCredits(condominiumId)));
    }

    @PostMapping("/calculate-cost")
    public ResponseEntity<Map<String, Integer>> calculateCost(
            @RequestBody @Valid GenerateInsightRequest request) {
        return ResponseEntity.ok(Map.of("cost", insightService.calculateCost(request)));
    }

    @PostMapping("/generate")
    public ResponseEntity<InsightGenerationResponse> generate(
            @RequestBody @Valid GenerateInsightRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(insightService.generate(request, condominiumId));
    }

    @GetMapping("/generations")
    public ResponseEntity<List<InsightGenerationResponse>> listGenerations(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(insightService.listGenerations(condominiumId));
    }
}
