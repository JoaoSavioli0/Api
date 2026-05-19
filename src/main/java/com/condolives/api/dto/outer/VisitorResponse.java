package com.condolives.api.dto.outer;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.entity.Outer.Visitor;

public record VisitorResponse(
        UUID id,
        UUID condominiumId,
        UUID memberId,
        String memberName,
        String name,
        String document,
        String photoUrl,
        Instant expectedAt,
        Instant arrivedAt,
        Instant leftAt,
        String status,
        String statusDescricao,
        String notes,
        Instant createdAt) {

    public static VisitorResponse from(Visitor v, String memberName) {
        return new VisitorResponse(
                v.getId(),
                v.getCondominiumId(),
                v.getMemberId(),
                memberName,
                v.getName(),
                v.getDocument(),
                v.getPhotoUrl(),
                v.getExpectedAt(),
                v.getArrivedAt(),
                v.getLeftAt(),
                v.getStatus().toDbValue(),
                v.getStatus().getDescricao(),
                v.getNotes(),
                v.getCreatedAt());
    }
}
