package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.staff.CreateStaffRequest;
import com.condolives.api.dto.staff.StaffResponse;
import com.condolives.api.dto.staff.UpdateStaffRequest;
import com.condolives.api.enums.StaffCategory;

public interface StaffService {
    List<StaffResponse> list(UUID condominiumId, StaffCategory category);
    StaffResponse getById(UUID id, UUID condominiumId);
    StaffResponse create(CreateStaffRequest request, UUID condominiumId);
    StaffResponse update(UUID id, UpdateStaffRequest request, UUID condominiumId);
    void dismiss(UUID id, UUID condominiumId);
}
