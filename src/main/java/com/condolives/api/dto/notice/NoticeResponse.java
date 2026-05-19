package com.condolives.api.dto.notice;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.entity.Post.Notice;

public record NoticeResponse(
        UUID id,
        String title,
        String description,
        String importance,
        String targetType,
        String targetValue,
        long readCount,
        long targetCount,
        Instant createdAt) {

    public static NoticeResponse from(Notice notice, long readCount, long targetCount) {
        return new NoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getDescription(),
                notice.getImportance() != null ? notice.getImportance().toDbValue() : null,
                notice.getTargetType(),
                notice.getTargetValue(),
                readCount,
                targetCount,
                notice.getCreatedAt());
    }
}
