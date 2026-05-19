package com.condolives.api.repository.Insight;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.condolives.api.entity.Insight.InsightGeneration;

@Repository
public interface InsightGenerationRepository extends JpaRepository<InsightGeneration, UUID> {
    List<InsightGeneration> findByCondominiumIdOrderByGeneratedAtDesc(UUID condominiumId);
}
