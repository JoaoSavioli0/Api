package com.condolives.api.dto.serviceline;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.condolives.api.entity.ServiceLine.ServiceLine;

public record ServiceLineDetailResponse(
        UUID id,
        String title,
        String description,
        String status,
        String responsibleName,
        LocalDate startDate,
        String estimatedEndDate,
        String estimatedCost,
        UUID linkedRequestId,
        List<StepResponse> steps,
        Instant createdAt) {

    public static ServiceLineDetailResponse from(ServiceLine sl) {
        return new ServiceLineDetailResponse(
                sl.getId(),
                sl.getTitle(),
                sl.getDescription(),
                sl.getStatus(),
                sl.getResponsibleName(),
                sl.getStartDate(),
                sl.getEstimatedEndDate(),
                sl.getEstimatedCost(),
                sl.getLinkedRequestId(),
                sl.getSteps().stream().map(StepResponse::from).toList(),
                sl.getCreatedAt());
    }
}
