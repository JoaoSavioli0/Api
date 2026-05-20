package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.insight.GenerateInsightRequest;
import com.condolives.api.dto.insight.InsightGenerationResponse;

public interface InsightService {
    int getCredits(UUID condominiumId);
    int calculateCost(GenerateInsightRequest request);
    InsightGenerationResponse generate(GenerateInsightRequest request, UUID condominiumId);
    List<InsightGenerationResponse> listGenerations(UUID condominiumId);
}
