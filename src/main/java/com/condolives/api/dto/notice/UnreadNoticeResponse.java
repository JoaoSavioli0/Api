package com.condolives.api.dto.notice;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.entity.Post.Notice;

public record UnreadNoticeResponse(
        UUID id,
        String title,
        String description,
        String importance,
        Instant createdAt) {

    public static UnreadNoticeResponse from(Notice notice) {
        return new UnreadNoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getDescription(),
                notice.getImportance() != null ? notice.getImportance().toDbValue() : null,
                notice.getCreatedAt());
    }
}
