package com.condolives.api.repository.Outer;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.Outer.Visitor;

public interface VisitorRepository extends JpaRepository<Visitor, UUID> {

    long countByCondominiumIdAndCreatedAtBetween(UUID condominiumId, Instant start, Instant end);

    List<Visitor> findByCondominiumIdOrderByCreatedAtDesc(UUID condominiumId);

    Optional<Visitor> findByIdAndCondominiumId(UUID id, UUID condominiumId);
}
