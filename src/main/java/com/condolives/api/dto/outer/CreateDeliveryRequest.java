package com.condolives.api.dto.outer;

public record CreateDeliveryRequest(
        String sender,
        String description,
        String notes
) {}
