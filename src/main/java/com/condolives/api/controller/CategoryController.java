package com.condolives.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.category.CategoryResponse;
import com.condolives.api.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listCategories(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);

        return ResponseEntity.ok(categoryService.listCategories(condominiumId));
    }
}
