package com.condolives.api.repository.ServiceLine;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.ServiceLine.ServiceLine;

public interface ServiceLineRepository extends JpaRepository<ServiceLine, UUID> {
    List<ServiceLine> findByCondominiumIdOrderByCreatedAtDesc(UUID condominiumId);
    Optional<ServiceLine> findByIdAndCondominiumId(UUID id, UUID condominiumId);

    long countByCondominiumIdAndStatus(UUID condominiumId, String status);
}
