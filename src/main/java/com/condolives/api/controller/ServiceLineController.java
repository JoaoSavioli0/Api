package com.condolives.api.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.serviceline.CreateServiceLineRequest;
import com.condolives.api.dto.serviceline.CreateStepRequest;
import com.condolives.api.dto.serviceline.ServiceLineDetailResponse;
import com.condolives.api.dto.serviceline.ServiceLineListResponse;
import com.condolives.api.dto.serviceline.StepAttachmentResponse;
import com.condolives.api.dto.serviceline.StepResponse;
import com.condolives.api.dto.serviceline.UpdateServiceLineRequest;
import com.condolives.api.service.ServiceLineService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/service-lines")
@RequiredArgsConstructor
public class ServiceLineController {

    private final ServiceLineService serviceLineService;

    @GetMapping
    public ResponseEntity<List<ServiceLineListResponse>> list(Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(serviceLineService.list(condominiumId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceLineDetailResponse> create(
            @RequestBody @Valid CreateServiceLineRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceLineService.create(request, condominiumId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceLineDetailResponse> getDetail(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(serviceLineService.getDetail(id, condominiumId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceLineDetailResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateServiceLineRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(serviceLineService.update(id, request, condominiumId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        serviceLineService.delete(id, condominiumId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/steps")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StepResponse> createStep(
            @PathVariable UUID id,
            @RequestBody @Valid CreateStepRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceLineService.createStep(id, request, condominiumId));
    }

    @PutMapping("/{id}/steps/{stepId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StepResponse> updateStep(
            @PathVariable UUID id,
            @PathVariable UUID stepId,
            @RequestBody @Valid CreateStepRequest request,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.ok(serviceLineService.updateStep(id, stepId, request, condominiumId));
    }

    @DeleteMapping("/{id}/steps/{stepId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStep(
            @PathVariable UUID id,
            @PathVariable UUID stepId,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        serviceLineService.deleteStep(id, stepId, condominiumId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/steps/{stepId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StepAttachmentResponse> uploadAttachment(
            @PathVariable UUID id,
            @PathVariable UUID stepId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceLineService.uploadAttachment(id, stepId, file, name, type, condominiumId));
    }

    @GetMapping("/{id}/steps/{stepId}/attachments/{attachmentId}/download-url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getAttachmentDownloadUrl(
            @PathVariable UUID id,
            @PathVariable UUID stepId,
            @PathVariable UUID attachmentId,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        String url = serviceLineService.getAttachmentDownloadUrl(id, stepId, attachmentId, condominiumId);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @DeleteMapping("/{id}/steps/{stepId}/attachments/{attachmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable UUID id,
            @PathVariable UUID stepId,
            @PathVariable UUID attachmentId,
            Authentication authentication) {
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        serviceLineService.deleteAttachment(id, stepId, attachmentId, condominiumId);
        return ResponseEntity.noContent().build();
    }
}
