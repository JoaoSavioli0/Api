package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.dto.company.CompanyResponse;
import com.condolives.api.dto.company.CreateCompanyRequest;
import com.condolives.api.entity.User.Company;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.User.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<CompanyResponse> list(UUID condominiumId) {
        return companyRepository.findAllByCondominiumIdAndActive(condominiumId, true)
                .stream()
                .map(CompanyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyResponse getById(UUID id, UUID condominiumId) {
        return companyRepository.findByIdAndCondominiumId(id, condominiumId)
                .map(CompanyResponse::from)
                .orElseThrow(() -> new ServiceException("Empresa não encontrada", 404));
    }

    @Transactional
    public CompanyResponse create(CreateCompanyRequest request, UUID condominiumId) {
        if (companyRepository.existsByCondominiumIdAndNameIgnoreCase(condominiumId, request.name())) {
            throw new ServiceException("Já existe uma empresa com este nome neste condomínio", 409);
        }

        Company company = companyRepository.save(Company.builder()
                .condominiumId(condominiumId)
                .name(request.name())
                .cnpj(request.cnpj())
                .phone(request.phone())
                .email(request.email())
                .active(true)
                .build());

        return CompanyResponse.from(company);
    }
}
