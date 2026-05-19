package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.condolives.api.dto.category.CategoryResponse;
import com.condolives.api.entity.Post.Ticket.Category;
import com.condolives.api.repository.Post.Ticket.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> listCategories(UUID condominiumId) {
        List<Category> categories = categoryRepository.findAllByCondominiumId(condominiumId);
        return categories.stream().map(CategoryResponse::from).toList();
    }
}
