package com.condolives.api.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.RevokedToken;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, UUID> {

    boolean existsByJti(UUID jti);

    void deleteAllByExpiresAtBefore(Instant cutoff);
}
