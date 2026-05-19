package com.condolives.api.dto.insight;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.entity.Insight.Insight;

public record InsightResponse(
        UUID id,
        String category,
        String severity,
        String title,
        String description,
        String actionLabel,
        Instant createdAt) {

    public static InsightResponse from(Insight insight) {
        return new InsightResponse(
                insight.getId(),
                insight.getCategory(),
                insight.getSeverity(),
                insight.getTitle(),
                insight.getDescription(),
                insight.getActionLabel(),
                insight.getCreatedAt());
    }
}
