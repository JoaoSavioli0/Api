package com.condolives.api.repository.User;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.User.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByMemberIdAndCondominiumIdOrderByCreatedAtDesc(UUID memberId, UUID condominiumId);
}
