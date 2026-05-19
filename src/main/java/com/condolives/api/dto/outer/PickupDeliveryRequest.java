package com.condolives.api.dto.outer;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record PickupDeliveryRequest(
        @NotNull UUID pickedUpBy
) {}
