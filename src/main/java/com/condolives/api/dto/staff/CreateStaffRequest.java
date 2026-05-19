package com.condolives.api.dto.staff;

import java.time.LocalDate;
import java.util.UUID;

import com.condolives.api.enums.StaffCategory;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateStaffRequest(
        @NotBlank String name,
        String phone,
        @Pattern(regexp = "\\d{11}") String cpf,
        String rg,
        String jobTitle,
        @NotNull StaffCategory category,
        String address,
        UUID companyId,
        LocalDate joinedAt,
        Boolean createAccess,
        @Email String email) {
}
