package com.condolives.api.dto.member;

import java.util.UUID;

import com.condolives.api.entity.User.CondoMember;

public record MemberNameResponse(
        UUID id,
        String name,
        boolean active) {
    public static MemberNameResponse from(CondoMember m) {
        return new MemberNameResponse(
                m.getId(),
                m.getUser().getName(),
                m.getActive());
    }
}
