package com.condolives.api.dto.member;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.entity.User.UserAccount;
import com.condolives.api.enums.MemberRole;

public record MemberListResponse(
        UUID id,
        UUID condominiumId,
        String name,
        String email,
        String cpf,
        String rg,
        String phone,
        MemberRole role,
        List<MemberNameResponse> dependents,
        UUID unitId,
        String unitAddress,
        String avatarUrl,
        UUID guardianId,
        LocalDate joinedAt,
        Boolean active) {
    public static MemberListResponse from(CondoMember m) {
        UserAccount u = m.getUser() != null ? m.getUser() : UserAccount.builder().build();
        return new MemberListResponse(
                m.getId(),
                m.getCondominiumId(),
                u.getName() != null ? u.getName() : "Membro sem conta",
                u.getEmail() != null ? u.getEmail() : "-",
                u.getCpf() != null ? u.getCpf() : "-",
                u.getRg() != null ? u.getRg() : "-",
                u.getPhone() != null ? u.getPhone() : "-",
                m.getRole(),
                m.getDependents().stream().map(MemberNameResponse::from).toList(),
                m.getUnitId(),
                m.getUnitAddress(),
                u.getAvatarUrl() != null ? u.getAvatarUrl() : "",
                m.getGuardianId(),
                m.getJoinedAt(),
                m.getActive());
    }
}
