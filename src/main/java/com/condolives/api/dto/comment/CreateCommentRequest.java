package com.condolives.api.dto.comment;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotNull
    private String content;
    @NotNull
    private UUID postId;
}
