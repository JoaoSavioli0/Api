package com.condolives.api.service;

import java.time.Instant;
import java.util.UUID;

public interface TokenRevocationService {
    void revokeToken(UUID jti, Instant expiresAt);
    boolean isRevoked(UUID jti);
}
