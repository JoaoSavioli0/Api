package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.dto.unit.CreateUnitRequest;
import com.condolives.api.dto.unit.UnitResponse;
import com.condolives.api.entity.User.Unit;
import com.condolives.api.enums.UnitType;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.User.UnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Transactional
    public UnitResponse createUnit(CreateUnitRequest request, UUID condominiumId) {
        UnitType type = parseUnitType(request.type());

        Unit unit = Unit.builder()
                .condominiumId(condominiumId)
                .identifier(request.identifier())
                .block(request.block())
                .street(request.street())
                .floor(request.floor())
                .type(type)
                .build();

        return UnitResponse.from(unitRepository.save(unit));
    }

    @Transactional(readOnly = true)
    public List<UnitResponse> listUnits(UUID condominiumId) {
        return unitRepository.findAllByCondominiumIdOrderByTypeAscIdentifierAsc(condominiumId)
                .stream()
                .map(UnitResponse::from)
                .toList();
    }

    @Transactional
    public void deleteUnit(UUID unitId, UUID condominiumId) {
        Unit unit = unitRepository.findByIdAndCondominiumId(unitId, condominiumId)
                .orElseThrow(() -> new ServiceException("Unidade não encontrada", 404));
        unitRepository.delete(unit);
    }

    private UnitType parseUnitType(String s) {
        try {
            return UnitType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Tipo de unidade inválido. Valores: APARTMENT, HOUSE", 422);
        }
    }
}
