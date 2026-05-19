package com.condolives.api.dto.condominium;

import java.util.UUID;

import com.condolives.api.entity.Condominium;

public record CondominiumResponse(
        UUID id,
        String name,
        String address) {

    public static CondominiumResponse from(Condominium c) {
        return new CondominiumResponse(
                c.getId(),
                c.getName(),
                c.getAddress());
    }
}