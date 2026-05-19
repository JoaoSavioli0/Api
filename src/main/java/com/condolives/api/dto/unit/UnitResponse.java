package com.condolives.api.dto.unit;

import java.util.UUID;

import com.condolives.api.entity.User.Unit;
import com.condolives.api.enums.UnitType;

public record UnitResponse(
        UUID id,
        UUID condominiumId,
        String identifier,
        String block,
        String street,
        Integer floor,
        UnitType type,
        String displayAddress) {

    public static UnitResponse from(Unit unit) {
        return new UnitResponse(
                unit.getId(),
                unit.getCondominiumId(),
                unit.getIdentifier(),
                unit.getBlock(),
                unit.getStreet(),
                unit.getFloor(),
                unit.getType(),
                unit.getDisplayAddress());
    }
}
