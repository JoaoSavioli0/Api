package com.condolives.api.dto.serviceline;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.condolives.api.entity.ServiceLine.ServiceLine;

public record ServiceLineListResponse(
        UUID id,
        String title,
        String status,
        String responsibleName,
        LocalDate startDate,
        String estimatedEndDate,
        UUID linkedRequestId,
        long stepCount,
        Instant createdAt) {

    public static ServiceLineListResponse from(ServiceLine sl, long stepCount) {
        return new ServiceLineListResponse(
                sl.getId(),
                sl.getTitle(),
                sl.getStatus(),
                sl.getResponsibleName(),
                sl.getStartDate(),
                sl.getEstimatedEndDate(),
                sl.getLinkedRequestId(),
                stepCount,
                sl.getCreatedAt());
    }
}
