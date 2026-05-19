package com.condolives.api.dto.staff;

import java.util.UUID;

import com.condolives.api.enums.StaffCategory;

public record UpdateStaffRequest(
        String name,
        String phone,
        String cpf,
        String rg,
        String jobTitle,
        StaffCategory category,
        String address,
        UUID companyId) {
}
