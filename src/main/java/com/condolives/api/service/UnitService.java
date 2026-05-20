package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.unit.CreateUnitRequest;
import com.condolives.api.dto.unit.UnitResponse;

public interface UnitService {
    UnitResponse createUnit(CreateUnitRequest request, UUID condominiumId);
    List<UnitResponse> listUnits(UUID condominiumId);
    void deleteUnit(UUID unitId, UUID condominiumId);
}
