package com.condolives.api.dto.serviceline;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStepRequest(
        @NotBlank String title,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        boolean isPublic,
        String note,
        List<CreateContributorRequest> contributors,
        List<CreateAttachmentRequest> attachments) {
}
