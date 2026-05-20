package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.company.CompanyResponse;
import com.condolives.api.dto.company.CreateCompanyRequest;

public interface CompanyService {
    List<CompanyResponse> list(UUID condominiumId);
    CompanyResponse getById(UUID id, UUID condominiumId);
    CompanyResponse create(CreateCompanyRequest request, UUID condominiumId);
}
