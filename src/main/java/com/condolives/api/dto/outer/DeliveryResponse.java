package com.condolives.api.dto.outer;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.entity.Outer.Delivery;

public record DeliveryResponse(
        UUID id,
        UUID condominiumId,
        UUID receivedBy,
        String receivedByName,
        UUID pickedUpBy,
        String pickedUpByName,
        String sender,
        String description,
        String photoUrl,
        Instant receivedAt,
        Instant pickedUpAt,
        String notes,
        boolean pending) {

    public static DeliveryResponse from(Delivery d, String receivedByName, String pickedUpByName) {
        return new DeliveryResponse(
                d.getId(),
                d.getCondominiumId(),
                d.getReceivedBy(),
                receivedByName,
                d.getPickedUpBy(),
                pickedUpByName,
                d.getSender(),
                d.getDescription(),
                d.getPhotoUrl(),
                d.getReceivedAt(),
                d.getPickedUpAt(),
                d.getNotes(),
                d.getPickedUpBy() == null);
    }
}
