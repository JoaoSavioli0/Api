package com.condolives.api.repository.ServiceLine;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.ServiceLine.ServiceStepAttachment;

public interface ServiceStepAttachmentRepository extends JpaRepository<ServiceStepAttachment, UUID> {
    Optional<ServiceStepAttachment> findByIdAndStepId(UUID id, UUID stepId);
}
