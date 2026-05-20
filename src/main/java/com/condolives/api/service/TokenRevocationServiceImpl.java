package com.condolives.api.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.entity.RevokedToken;
import com.condolives.api.repository.RevokedTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenRevocationServiceImpl implements TokenRevocationService {

    private final RevokedTokenRepository revokedTokenRepository;

    @Transactional
    public void revokeToken(UUID jti, Instant expiresAt) {
        if (!revokedTokenRepository.existsByJti(jti)) {
            revokedTokenRepository.save(RevokedToken.builder()
                    .jti(jti)
                    .expiresAt(expiresAt)
                    .build());
        }
    }

    public boolean isRevoked(UUID jti) {
        return revokedTokenRepository.existsByJti(jti);
    }

    /** Remove tokens expirados da blacklist a cada hora. */
    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void purgeExpiredTokens() {
        revokedTokenRepository.deleteAllByExpiresAtBefore(Instant.now());
    }
}
