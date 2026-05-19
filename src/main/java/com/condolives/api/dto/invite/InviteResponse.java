package com.condolives.api.dto.invite;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.entity.User.MemberInvite;
import com.condolives.api.enums.MemberRole;

public record InviteResponse(
        UUID id,
        UUID memberId,
        String unitAddress,
        MemberRole role,
        String token,
        boolean claimed,
        Instant expiresAt,
        Instant usedAt,
        Instant createdAt) {

    public static InviteResponse from(MemberInvite invite, CondoMember member) {
        return new InviteResponse(
                invite.getId(),
                member.getId(),
                member.getUnitAddress(),
                member.getRole(),
                invite.getToken(),
                invite.getUsedAt() != null,
                invite.getExpiresAt(),
                invite.getUsedAt(),
                invite.getCreatedAt());
    }
}
