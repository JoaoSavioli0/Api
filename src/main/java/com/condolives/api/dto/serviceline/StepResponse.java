package com.condolives.api.dto.serviceline;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.condolives.api.entity.ServiceLine.ServiceStep;

public record StepResponse(
        UUID id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        boolean isPublic,
        String note,
        List<StepContributorResponse> contributors,
        List<StepAttachmentResponse> attachments) {

    public static StepResponse from(ServiceStep step) {
        return new StepResponse(
                step.getId(),
                step.getTitle(),
                step.getStartDate(),
                step.getEndDate(),
                step.getIsPublic(),
                step.getNote(),
                step.getContributors().stream().map(StepContributorResponse::from).toList(),
                step.getAttachments().stream().map(StepAttachmentResponse::from).toList());
    }
}
