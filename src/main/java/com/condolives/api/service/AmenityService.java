package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.amenity.AmenityDetailResponse;
import com.condolives.api.dto.amenity.AmenityDetailResponseAdmin;
import com.condolives.api.dto.amenity.AmenityResponse;
import com.condolives.api.dto.amenity.CreateAmenityRequest;
import com.condolives.api.dto.amenity.CreateExceptionRequest;
import com.condolives.api.dto.amenity.ExceptionResponse;
import com.condolives.api.dto.amenity.ScheduleResponse;
import com.condolives.api.dto.amenity.SetScheduleRequest;
import com.condolives.api.dto.amenity.UpdateAmenityRequest;

public interface AmenityService {
    AmenityResponse create(CreateAmenityRequest request, UUID condominiumId);
    List<AmenityResponse> list(UUID condominiumId, Boolean activeOnly);
    AmenityDetailResponse getDetail(UUID id, UUID condominiumId);
    AmenityDetailResponseAdmin getDetailAdmin(UUID id, UUID condominiumId);
    AmenityResponse update(UUID id, UpdateAmenityRequest request, UUID condominiumId);
    ScheduleResponse setSchedule(UUID amenityId, short dayOfWeek, SetScheduleRequest request, UUID condominiumId);
    void deleteSchedule(UUID amenityId, short dayOfWeek, UUID condominiumId);
    ExceptionResponse addException(UUID amenityId, CreateExceptionRequest request, UUID condominiumId);
    void deleteException(UUID exceptionId, UUID condominiumId);
}
