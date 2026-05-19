package com.condolives.api.dto.invite;

import java.util.UUID;

public record CreateInviteRequest(
        UUID unitId,
        String role,
        UUID guardianId) {
}
