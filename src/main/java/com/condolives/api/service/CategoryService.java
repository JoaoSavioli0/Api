package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.category.CategoryResponse;

public interface CategoryService {
    List<CategoryResponse> listCategories(UUID condominiumId);
}
