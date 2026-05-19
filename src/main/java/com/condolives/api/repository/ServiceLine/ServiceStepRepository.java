package com.condolives.api.repository.ServiceLine;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.ServiceLine.ServiceStep;

public interface ServiceStepRepository extends JpaRepository<ServiceStep, UUID> {
    Optional<ServiceStep> findByIdAndServiceLineId(UUID id, UUID serviceLineId);
    long countByServiceLineId(UUID serviceLineId);
}
