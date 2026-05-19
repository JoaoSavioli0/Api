package com.condolives.api.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.dto.outer.CreateVisitorRequest;
import com.condolives.api.dto.outer.DeliveryResponse;
import com.condolives.api.dto.outer.VisitorArrivalRequest;
import com.condolives.api.dto.outer.VisitorResponse;
import com.condolives.api.entity.Outer.Delivery;
import com.condolives.api.entity.Outer.Visitor;
import com.condolives.api.entity.User.Notification;
import com.condolives.api.enums.NotificationType;
import com.condolives.api.enums.VisitorStatus;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Outer.DeliveryRepository;
import com.condolives.api.repository.Outer.VisitorRepository;
import com.condolives.api.repository.User.CondoMemberRepository;
import com.condolives.api.repository.User.NotificationRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GatehouseService {

    private final DeliveryRepository deliveryRepository;
    private final VisitorRepository visitorRepository;
    private final NotificationRepository notificationRepository;
    private final CondoMemberRepository condoMemberRepository;
    private final ImageStorageService imageStorageService;

    public List<DeliveryResponse> listDeliveries(UUID condominiumId) {
        return deliveryRepository.findByCondominiumIdOrderByReceivedAtDesc(condominiumId)
                .stream()
                .map(d -> buildResponse(d, condominiumId))
                .toList();
    }

    @Transactional
    public DeliveryResponse registerDelivery(String sender, String description, String notes,
            MultipartFile photo, UUID receivedBy, UUID condominiumId) {
        String photoUrl = null;
        if (photo != null && !photo.isEmpty()) {
            photoUrl = imageStorageService.uploadImages(List.of(photo)).get(0);
        }

        Delivery delivery = Delivery.builder()
                .condominiumId(condominiumId)
                .receivedBy(receivedBy)
                .sender(sender)
                .description(description)
                .notes(notes)
                .photoUrl(photoUrl)
                .build();

        return buildResponse(deliveryRepository.save(delivery), condominiumId);
    }

    @Transactional
    public DeliveryResponse pickupDelivery(UUID deliveryId, UUID pickedUpBy, UUID condominiumId) {
        Delivery delivery = deliveryRepository
                .findByIdAndCondominiumId(deliveryId, condominiumId)
                .orElseThrow(() -> new ServiceException("Entrega não encontrada", 404));

        if (delivery.getPickedUpBy() != null) {
            throw new ServiceException("Entrega já foi retirada", 409);
        }

        delivery.setPickedUpAt(Instant.now());
        delivery.setPickedUpBy(pickedUpBy);
        deliveryRepository.save(delivery);

        notificationRepository.save(Notification.builder()
                .condominiumId(condominiumId)
                .memberId(pickedUpBy)
                .type(NotificationType.DELIVERY)
                .title("Encomenda retirada")
                .body("Sua encomenda de " + delivery.getSender() + " foi retirada na portaria.")
                .referenceId(delivery.getId())
                .referenceTable("delivery")
                .read(false)
                .build());

        return buildResponse(delivery, condominiumId);
    }

    public VisitorResponse preAuthorizeVisitor(@Valid CreateVisitorRequest request, UUID residentId, UUID condominiumId) {
        Visitor visitor = Visitor.builder()
                .condominiumId(condominiumId)
                .memberId(residentId)
                .name(request.name())
                .document(request.document())
                .expectedAt(request.expectedAt())
                .notes(request.notes())
                .build();

        return buildVisitorResponse(visitorRepository.save(visitor), condominiumId);
    }

    // ── Visitor admin endpoints ───────────────────────────────────────────────

    public List<VisitorResponse> listVisitors(UUID condominiumId) {
        return visitorRepository.findByCondominiumIdOrderByCreatedAtDesc(condominiumId)
                .stream()
                .map(v -> buildVisitorResponse(v, condominiumId))
                .toList();
    }

    @Transactional
    public VisitorResponse registerVisitor(String name, String document, Instant expectedAt,
            String notes, MultipartFile photo, UUID memberId, UUID condominiumId) {
        String photoUrl = null;
        if (photo != null && !photo.isEmpty()) {
            photoUrl = imageStorageService.uploadImages(List.of(photo)).get(0);
        }

        Visitor visitor = Visitor.builder()
                .condominiumId(condominiumId)
                .memberId(memberId)
                .name(name)
                .document(document)
                .expectedAt(expectedAt)
                .notes(notes)
                .photoUrl(photoUrl)
                .build();

        return buildVisitorResponse(visitorRepository.save(visitor), condominiumId);
    }

    @Transactional
    public VisitorResponse recordArrival(UUID visitorId, VisitorArrivalRequest request, UUID condominiumId) {
        Visitor visitor = visitorRepository.findByIdAndCondominiumId(visitorId, condominiumId)
                .orElseThrow(() -> new ServiceException("Visita não encontrada", 404));

        if (visitor.getStatus() != VisitorStatus.AGUARDANDO) {
            throw new ServiceException("Status inválido para registrar chegada", 409);
        }

        if (request.document() != null && !request.document().isBlank()) {
            visitor.setDocument(request.document());
        }
        visitor.setArrivedAt(Instant.now());
        visitor.setStatus(VisitorStatus.CHEGOU);
        visitorRepository.save(visitor);

        return buildVisitorResponse(visitor, condominiumId);
    }

    @Transactional
    public VisitorResponse recordDeparture(UUID visitorId, UUID condominiumId) {
        Visitor visitor = visitorRepository.findByIdAndCondominiumId(visitorId, condominiumId)
                .orElseThrow(() -> new ServiceException("Visita não encontrada", 404));

        if (visitor.getStatus() != VisitorStatus.CHEGOU) {
            throw new ServiceException("Visitante não está registrado como presente", 409);
        }

        visitor.setLeftAt(Instant.now());
        visitor.setStatus(VisitorStatus.SAIU);
        visitorRepository.save(visitor);

        return buildVisitorResponse(visitor, condominiumId);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private VisitorResponse buildVisitorResponse(Visitor v, UUID condominiumId) {
        String memberName = resolveName(v.getMemberId(), condominiumId);
        return VisitorResponse.from(v, memberName);
    }

    private DeliveryResponse buildResponse(Delivery d, UUID condominiumId) {
        String receivedByName = resolveName(d.getReceivedBy(), condominiumId);
        String pickedUpByName = d.getPickedUpBy() != null ? resolveName(d.getPickedUpBy(), condominiumId) : null;
        return DeliveryResponse.from(d, receivedByName, pickedUpByName);
    }

    private String resolveName(UUID memberId, UUID condominiumId) {
        return condoMemberRepository.findByIdAndCondominiumId(memberId, condominiumId)
                .map(m -> m.getUser().getName())
                .orElse("Desconhecido");
    }
}
