package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.notification.NotificationResponse;

public interface NotificationService {
    List<NotificationResponse> getNotifications(UUID residentId, UUID condominiumId);
}
