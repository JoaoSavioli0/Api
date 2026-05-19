package com.condolives.api.dto.like;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLikeRequest {
    @NotNull
    private UUID postId;
}
