package com.condolives.api.dto.member;

public record UserUpdateProfileRequest(
        String phone,
        String unitAddress,
        String avatarUrl,
        String email) {
    public static UserUpdateProfileRequest of(MemberResponse m) {
        return new UserUpdateProfileRequest(
                m.phone(),
                m.unitAddress(),
                m.avatarUrl(),
                m.email());
    }
}
