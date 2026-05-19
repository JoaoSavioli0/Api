package com.condolives.api.dto.member;

import java.util.UUID;

public record UpdateMemberRequest(String name, String phone, UUID unitId) {
}
