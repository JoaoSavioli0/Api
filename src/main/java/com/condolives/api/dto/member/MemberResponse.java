package com.condolives.api.dto.member;

import java.time.LocalDate;
import java.util.UUID;

import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.entity.User.UserAccount;

public record MemberResponse(
        UUID      id,
        UUID      condominiumId,
        String    name,
        String    email,
        String    cpf,
        String    rg,
        String    phone,
        String    unitAddress,
        String    avatarUrl,
        UUID      guardianId,
        LocalDate joinedAt,
        Boolean   active
) {
    public static MemberResponse from(CondoMember m) {
        UserAccount u = m.getUser();
        return new MemberResponse(
                m.getId(),
                m.getCondominiumId(),
                u.getName(),
                u.getEmail(),
                u.getCpf(),
                u.getRg(),
                u.getPhone(),
                m.getUnitAddress(),
                u.getAvatarUrl(),
                m.getGuardianId(),
                m.getJoinedAt(),
                m.getActive()
        );
    }
}
