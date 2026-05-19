package com.condolives.api.dto.company;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.entity.User.Company;

public record CompanyResponse(
        UUID id,
        UUID condominiumId,
        String name,
        String cnpj,
        String phone,
        String email,
        Boolean active,
        Instant createdAt) {

    public static CompanyResponse from(Company c) {
        return new CompanyResponse(
                c.getId(),
                c.getCondominiumId(),
                c.getName(),
                c.getCnpj(),
                c.getPhone(),
                c.getEmail(),
                c.getActive(),
                c.getCreatedAt());
    }
}
