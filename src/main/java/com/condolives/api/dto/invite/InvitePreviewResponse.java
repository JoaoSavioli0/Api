package com.condolives.api.dto.invite;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.enums.MemberRole;

public record InvitePreviewResponse(
        UUID id,
        String condominiumName,
        String unitAddress,
        MemberRole role,
        Instant expiresAt) {
}
