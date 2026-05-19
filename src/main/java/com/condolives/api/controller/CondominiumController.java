package com.condolives.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.dto.condominium.CondominiumResponse;
import com.condolives.api.service.CondominiumService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/condominium")
@RequiredArgsConstructor
public class CondominiumController {
    private final CondominiumService condominiumService;

    @GetMapping("/code/{code}")
    public ResponseEntity<CondominiumResponse> getCondominium(@PathVariable String code) {
        return ResponseEntity.ok(condominiumService.findCondominiumByCode(code));
    }

}
