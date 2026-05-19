package com.condolives.api.dto.category;

import java.util.UUID;

import com.condolives.api.entity.Post.Ticket.Category;

public record CategoryResponse(UUID id, String name) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}