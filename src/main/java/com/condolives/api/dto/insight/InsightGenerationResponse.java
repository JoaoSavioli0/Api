package com.condolives.api.dto.insight;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.condolives.api.entity.Insight.Insight;
import com.condolives.api.entity.Insight.InsightGeneration;

public record InsightGenerationResponse(
        UUID id,
        Instant generatedAt,
        int creditsUsed,
        LocalDate periodStart,
        LocalDate periodEnd,
        String analysisDepth,
        List<String> dataSources,
        String status,
        List<InsightResponse> insights) {

    public static InsightGenerationResponse from(InsightGeneration gen, List<Insight> insights) {
        List<String> sources = gen.getDataSources().isBlank()
                ? List.of()
                : Arrays.asList(gen.getDataSources().split(","));
        return new InsightGenerationResponse(
                gen.getId(),
                gen.getGeneratedAt(),
                gen.getCreditsUsed(),
                gen.getPeriodStart(),
                gen.getPeriodEnd(),
                gen.getAnalysisDepth(),
                sources,
                gen.getStatus(),
                insights.stream().map(InsightResponse::from).toList());
    }
}
