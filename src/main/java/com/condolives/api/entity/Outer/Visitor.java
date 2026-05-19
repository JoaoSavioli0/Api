package com.condolives.api.entity.Outer;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.condolives.api.converter.VisitorStatusConverter;
import com.condolives.api.enums.VisitorStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "visitor")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "condominium_id", nullable = false, updatable = false)
    private UUID condominiumId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private UUID memberId;

    @Column(nullable = false)
    private String name;

    @Setter
    private String document;

    @Setter
    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "expected_at")
    private Instant expectedAt;

    @Setter
    @Column(name = "arrived_at")
    private Instant arrivedAt;

    @Setter
    @Column(name = "left_at")
    private Instant leftAt;

    @Setter
    @Builder.Default
    @Convert(converter = VisitorStatusConverter.class)
    @Column(nullable = false)
    private VisitorStatus status = VisitorStatus.AGUARDANDO;

    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
