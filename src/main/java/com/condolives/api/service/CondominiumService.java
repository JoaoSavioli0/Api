package com.condolives.api.service;

import org.springframework.stereotype.Service;

import com.condolives.api.dto.condominium.CondominiumResponse;
import com.condolives.api.entity.Condominium;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Condominium.CondominiumRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CondominiumService {
    private final CondominiumRepository condominiumRepository;

    public CondominiumResponse findCondominiumByCode(String code) {
        Condominium condominium = condominiumRepository.findByCode(code)
                .orElseThrow(() -> new ServiceException("Código do condomínio não encontrado", 404));

        if (!condominium.getActive()) {
            throw new ServiceException("Condomínio inválido", 403);
        }

        return CondominiumResponse.from(condominium);
    }
}
