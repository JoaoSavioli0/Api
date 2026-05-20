package com.condolives.api.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.dto.serviceline.CreateAttachmentRequest;
import com.condolives.api.dto.serviceline.CreateContributorRequest;
import com.condolives.api.dto.serviceline.CreateServiceLineRequest;
import com.condolives.api.dto.serviceline.CreateStepRequest;
import com.condolives.api.dto.serviceline.ServiceLineDetailResponse;
import com.condolives.api.dto.serviceline.ServiceLineListResponse;
import com.condolives.api.dto.serviceline.StepAttachmentResponse;
import com.condolives.api.dto.serviceline.StepResponse;
import com.condolives.api.dto.serviceline.UpdateServiceLineRequest;
import com.condolives.api.entity.ServiceLine.ServiceLine;
import com.condolives.api.entity.ServiceLine.ServiceStep;
import com.condolives.api.entity.ServiceLine.ServiceStepAttachment;
import com.condolives.api.entity.ServiceLine.StepContributor;
import com.condolives.api.entity.User.Company;
import com.condolives.api.entity.User.Notification;
import com.condolives.api.entity.User.Staff;
import com.condolives.api.entity.User.UserAccount;
import com.condolives.api.enums.NotificationType;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Post.Ticket.TicketRepository;
import com.condolives.api.repository.ServiceLine.ServiceLineRepository;
import com.condolives.api.repository.ServiceLine.ServiceStepAttachmentRepository;
import com.condolives.api.repository.ServiceLine.ServiceStepRepository;
import com.condolives.api.repository.User.CompanyRepository;
import com.condolives.api.repository.User.NotificationRepository;
import com.condolives.api.repository.User.StaffRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceLineServiceImpl implements ServiceLineService {

    private static final Set<String> VALID_STATUSES = Set.of(
            "planejado", "em_andamento", "pausado", "concluido");

    private static final Set<String> VALID_SOURCE_TYPES = Set.of(
            "interno", "externo", "empresa", "nao_registrado");

    private final ServiceLineRepository serviceLineRepository;
    private final ServiceStepRepository serviceStepRepository;
    private final ServiceStepAttachmentRepository attachmentRepository;
    private final StaffRepository staffRepository;
    private final CompanyRepository companyRepository;
    private final TicketRepository ticketRepository;
    private final NotificationRepository notificationRepository;
    private final ImageStorageService imageStorageService;

    public List<ServiceLineListResponse> list(UUID condominiumId) {
        return serviceLineRepository.findByCondominiumIdOrderByCreatedAtDesc(condominiumId)
                .stream()
                .map(sl -> ServiceLineListResponse.from(sl,
                        serviceStepRepository.countByServiceLineId(sl.getId())))
                .toList();
    }

    @Transactional
    public ServiceLineDetailResponse create(CreateServiceLineRequest request, UUID condominiumId) {
        ServiceLine line = ServiceLine.builder()
                .condominiumId(condominiumId)
                .title(request.title())
                .description(request.description())
                .status("planejado")
                .responsibleName(request.responsibleName())
                .startDate(request.startDate())
                .estimatedEndDate(request.estimatedEndDate())
                .estimatedCost(request.estimatedCost())
                .linkedRequestId(request.linkedRequestId())
                .build();

        return ServiceLineDetailResponse.from(serviceLineRepository.save(line));
    }

    @Transactional(readOnly = true)
    public ServiceLineDetailResponse getDetail(UUID id, UUID condominiumId) {
        ServiceLine line = findLineOrThrow(id, condominiumId);
        return ServiceLineDetailResponse.from(line);
    }

    @Transactional
    public ServiceLineDetailResponse update(UUID id, UpdateServiceLineRequest request, UUID condominiumId) {
        if (!VALID_STATUSES.contains(request.status())) {
            throw new ServiceException(
                    "Status inválido. Valores aceitos: planejado, em_andamento, pausado, concluido", 422);
        }

        ServiceLine line = findLineOrThrow(id, condominiumId);
        boolean newLink = request.linkedRequestId() != null
                && !request.linkedRequestId().equals(line.getLinkedRequestId());

        line.setTitle(request.title());
        line.setDescription(request.description());
        line.setStatus(request.status());
        line.setResponsibleName(request.responsibleName());
        line.setStartDate(request.startDate());
        line.setEstimatedEndDate(request.estimatedEndDate());
        line.setEstimatedCost(request.estimatedCost());
        line.setLinkedRequestId(request.linkedRequestId());

        ServiceLine saved = serviceLineRepository.save(line);

        if (newLink) {
            ticketRepository.findById(request.linkedRequestId()).ifPresent(ticket ->
                notificationRepository.save(Notification.builder()
                        .condominiumId(condominiumId)
                        .memberId(ticket.getMemberId())
                        .type(NotificationType.TICKET_UPDATE)
                        .title("Solicitação vinculada a um serviço")
                        .body("Sua solicitação foi vinculada à linha de serviço \"" + saved.getTitle() + "\".")
                        .referenceId(saved.getId())
                        .referenceTable("service_lines")
                        .read(false)
                        .build()));
        }

        return ServiceLineDetailResponse.from(saved);
    }

    @Transactional
    public void delete(UUID id, UUID condominiumId) {
        ServiceLine line = findLineOrThrow(id, condominiumId);
        serviceLineRepository.delete(line);
    }

    @Transactional
    public StepResponse createStep(UUID serviceLineId, CreateStepRequest request, UUID condominiumId) {
        ServiceLine line = findLineOrThrow(serviceLineId, condominiumId);

        long nextIndex = serviceStepRepository.countByServiceLineId(serviceLineId);

        ServiceStep step = ServiceStep.builder()
                .serviceLine(line)
                .title(request.title())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .isPublic(request.isPublic())
                .note(request.note())
                .orderIndex((int) nextIndex)
                .build();

        addContributors(step, request.contributors(), condominiumId);
        addAttachmentsFromRequest(step, request.attachments());

        ServiceStep savedStep = serviceStepRepository.save(step);

        if (request.isPublic() && line.getLinkedRequestId() != null) {
            ticketRepository.findById(line.getLinkedRequestId()).ifPresent(ticket ->
                notificationRepository.save(Notification.builder()
                        .condominiumId(condominiumId)
                        .memberId(ticket.getMemberId())
                        .type(NotificationType.TIMELINE_UPDATE)
                        .title("Nova etapa no serviço vinculado")
                        .body("Uma nova etapa pública foi adicionada: \"" + request.title() + "\".")
                        .referenceId(savedStep.getId())
                        .referenceTable("service_steps")
                        .read(false)
                        .build()));
        }

        return StepResponse.from(savedStep);
    }

    @Transactional
    public StepResponse updateStep(UUID serviceLineId, UUID stepId, CreateStepRequest request, UUID condominiumId) {
        findLineOrThrow(serviceLineId, condominiumId);
        ServiceStep step = findStepOrThrow(stepId, serviceLineId);

        step.setTitle(request.title());
        step.setStartDate(request.startDate());
        step.setEndDate(request.endDate());
        step.setIsPublic(request.isPublic());
        step.setNote(request.note());

        step.getContributors().clear();
        addContributors(step, request.contributors(), condominiumId);

        return StepResponse.from(serviceStepRepository.save(step));
    }

    @Transactional
    public void deleteStep(UUID serviceLineId, UUID stepId, UUID condominiumId) {
        findLineOrThrow(serviceLineId, condominiumId);
        ServiceStep step = findStepOrThrow(stepId, serviceLineId);
        serviceStepRepository.delete(step);
    }

    @Transactional
    public StepAttachmentResponse uploadAttachment(UUID serviceLineId, UUID stepId,
            MultipartFile file, String name, String type, UUID condominiumId) {
        findLineOrThrow(serviceLineId, condominiumId);
        ServiceStep step = findStepOrThrow(stepId, serviceLineId);

        String url = imageStorageService.uploadImages(List.of(file)).get(0);

        ServiceStepAttachment attachment = ServiceStepAttachment.builder()
                .step(step)
                .name(name)
                .type(type)
                .url(url)
                .fileSize((int) file.getSize())
                .build();

        return StepAttachmentResponse.from(attachmentRepository.save(attachment));
    }

    public String getAttachmentDownloadUrl(UUID serviceLineId, UUID stepId, UUID attachmentId, UUID condominiumId) {
        findLineOrThrow(serviceLineId, condominiumId);
        findStepOrThrow(stepId, serviceLineId);
        ServiceStepAttachment attachment = attachmentRepository.findByIdAndStepId(attachmentId, stepId)
                .orElseThrow(() -> new ServiceException("Anexo não encontrado", 404));
        return imageStorageService.generateSignedDownloadUrl(attachment.getUrl());
    }

    @Transactional
    public void deleteAttachment(UUID serviceLineId, UUID stepId, UUID attachmentId, UUID condominiumId) {
        findLineOrThrow(serviceLineId, condominiumId);
        findStepOrThrow(stepId, serviceLineId);

        ServiceStepAttachment attachment = attachmentRepository.findByIdAndStepId(attachmentId, stepId)
                .orElseThrow(() -> new ServiceException("Anexo não encontrado", 404));

        attachmentRepository.delete(attachment);
    }

    private ServiceLine findLineOrThrow(UUID id, UUID condominiumId) {
        return serviceLineRepository.findByIdAndCondominiumId(id, condominiumId)
                .orElseThrow(() -> new ServiceException("Linha de serviço não encontrada", 404));
    }

    private ServiceStep findStepOrThrow(UUID stepId, UUID serviceLineId) {
        return serviceStepRepository.findByIdAndServiceLineId(stepId, serviceLineId)
                .orElseThrow(() -> new ServiceException("Etapa não encontrada", 404));
    }

    private void addContributors(ServiceStep step, List<CreateContributorRequest> contributors, UUID condominiumId) {
        if (contributors == null) return;
        for (var c : contributors) {
            String sourceType = c.sourceType();
            if (!VALID_SOURCE_TYPES.contains(sourceType)) {
                throw new ServiceException("Tipo de contribuinte inválido: " + sourceType, 400);
            }

            String name;
            UUID staffId = null;
            UUID companyId = null;

            switch (sourceType) {
                case "interno", "externo" -> {
                    if (c.staffId() == null) {
                        throw new ServiceException("staffId é obrigatório para contribuinte " + sourceType, 400);
                    }
                    Staff staff = staffRepository.findByIdAndCondominiumId(c.staffId(), condominiumId)
                            .orElseThrow(() -> new ServiceException("Colaborador não encontrado", 404));
                    staffId = staff.getId();
                    UserAccount user = staff.getUser();
                    name = user != null ? user.getName() : staff.getName();
                }
                case "empresa" -> {
                    if (c.companyId() == null) {
                        throw new ServiceException("companyId é obrigatório para contribuinte do tipo empresa", 400);
                    }
                    Company company = companyRepository.findByIdAndCondominiumId(c.companyId(), condominiumId)
                            .orElseThrow(() -> new ServiceException("Empresa não encontrada", 404));
                    companyId = company.getId();
                    name = company.getName();
                }
                default -> {
                    // nao_registrado
                    if (c.name() == null || c.name().isBlank()) {
                        throw new ServiceException("name é obrigatório para contribuinte não registrado", 400);
                    }
                    name = c.name().trim();
                }
            }

            step.getContributors().add(StepContributor.builder()
                    .step(step)
                    .name(name)
                    .type(sourceType)
                    .role(c.role())
                    .staffId(staffId)
                    .companyId(companyId)
                    .build());
        }
    }

    private void addAttachmentsFromRequest(ServiceStep step, List<CreateAttachmentRequest> attachments) {
        if (attachments == null) return;
        for (var a : attachments) {
            step.getAttachments().add(ServiceStepAttachment.builder()
                    .step(step)
                    .name(a.name())
                    .type(a.type())
                    .url(a.url())
                    .fileSize(a.fileSize())
                    .build());
        }
    }
}
