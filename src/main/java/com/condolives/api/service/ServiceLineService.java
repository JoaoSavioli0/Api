package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.dto.serviceline.CreateServiceLineRequest;
import com.condolives.api.dto.serviceline.CreateStepRequest;
import com.condolives.api.dto.serviceline.ServiceLineDetailResponse;
import com.condolives.api.dto.serviceline.ServiceLineListResponse;
import com.condolives.api.dto.serviceline.StepAttachmentResponse;
import com.condolives.api.dto.serviceline.StepResponse;
import com.condolives.api.dto.serviceline.UpdateServiceLineRequest;

public interface ServiceLineService {
    List<ServiceLineListResponse> list(UUID condominiumId);
    ServiceLineDetailResponse create(CreateServiceLineRequest request, UUID condominiumId);
    ServiceLineDetailResponse getDetail(UUID id, UUID condominiumId);
    ServiceLineDetailResponse update(UUID id, UpdateServiceLineRequest request, UUID condominiumId);
    void delete(UUID id, UUID condominiumId);
    StepResponse createStep(UUID serviceLineId, CreateStepRequest request, UUID condominiumId);
    StepResponse updateStep(UUID serviceLineId, UUID stepId, CreateStepRequest request, UUID condominiumId);
    void deleteStep(UUID serviceLineId, UUID stepId, UUID condominiumId);
    StepAttachmentResponse uploadAttachment(UUID serviceLineId, UUID stepId, MultipartFile file,
            String name, String type, UUID condominiumId);
    String getAttachmentDownloadUrl(UUID serviceLineId, UUID stepId, UUID attachmentId, UUID condominiumId);
    void deleteAttachment(UUID serviceLineId, UUID stepId, UUID attachmentId, UUID condominiumId);
}
