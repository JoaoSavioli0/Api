package com.condolives.api.dto.notification;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.entity.User.Notification;
import com.condolives.api.enums.NotificationType;

public record NotificationResponse(
        UUID id,
        NotificationType type,
        String title,
        String body,
        UUID referenceId,
        String referenceTable,
        Boolean read,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType(),
                n.getTitle(),
                n.getBody(),
                n.getReferenceId(),
                n.getReferenceTable(),
                n.getRead(),
                n.getCreatedAt());
    }
}
