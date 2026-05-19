package com.condolives.api.dto.serviceline;

import jakarta.validation.constraints.NotBlank;

public record CreateAttachmentRequest(
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank String url,
        Integer fileSize) {
}
