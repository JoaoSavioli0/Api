package com.condolives.api.service;

import com.condolives.api.dto.condominium.CondominiumResponse;

public interface CondominiumService {
    CondominiumResponse findCondominiumByCode(String code);
}
