package com.condolives.api.dto.serviceline;

import java.util.UUID;

import com.condolives.api.entity.ServiceLine.ServiceStepAttachment;

public record StepAttachmentResponse(UUID id, String name, String type, String url, Integer fileSize) {

    public static StepAttachmentResponse from(ServiceStepAttachment a) {
        return new StepAttachmentResponse(a.getId(), a.getName(), a.getType(), a.getUrl(), a.getFileSize());
    }
}
