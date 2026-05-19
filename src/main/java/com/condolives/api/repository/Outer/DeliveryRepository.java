package com.condolives.api.repository.Outer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.Outer.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByIdAndCondominiumId(UUID id, UUID condominiumId);

    List<Delivery> findByCondominiumIdOrderByReceivedAtDesc(UUID condominiumId);
}
