package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.condolives.api.dto.notification.NotificationResponse;
import com.condolives.api.repository.User.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotifications(UUID residentId, UUID condominiumId) {
        return notificationRepository
                .findByMemberIdAndCondominiumIdOrderByCreatedAtDesc(residentId, condominiumId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
