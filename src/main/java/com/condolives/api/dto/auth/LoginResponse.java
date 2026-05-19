package com.condolives.api.dto.auth;

import java.util.UUID;

public record LoginResponse(
        String token,
        String type,
        UUID residentId,
        String name,
        String email,
        String unitAddress,
        String avatarUrl,
        UUID condominiumId) {
}
