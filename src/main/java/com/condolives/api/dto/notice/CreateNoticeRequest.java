package com.condolives.api.dto.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNoticeRequest(
        @NotBlank String title,
        String description,
        @NotNull String importance,
        @NotBlank String targetType,
        String targetValue) {
}
