package com.condolives.api.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.condolives.api.dto.outer.CreateVisitorRequest;
import com.condolives.api.dto.outer.DeliveryResponse;
import com.condolives.api.dto.outer.VisitorArrivalRequest;
import com.condolives.api.dto.outer.VisitorResponse;

public interface GatehouseService {
    List<DeliveryResponse> listDeliveries(UUID condominiumId);
    DeliveryResponse registerDelivery(String sender, String description, String notes,
            MultipartFile photo, UUID receivedBy, UUID condominiumId);
    DeliveryResponse pickupDelivery(UUID deliveryId, UUID pickedUpBy, UUID condominiumId);
    VisitorResponse preAuthorizeVisitor(CreateVisitorRequest request, UUID residentId, UUID condominiumId);
    List<VisitorResponse> listVisitors(UUID condominiumId);
    VisitorResponse registerVisitor(String name, String document, Instant expectedAt,
            String notes, MultipartFile photo, UUID memberId, UUID condominiumId);
    VisitorResponse recordArrival(UUID visitorId, VisitorArrivalRequest request, UUID condominiumId);
    VisitorResponse recordDeparture(UUID visitorId, UUID condominiumId);
}
