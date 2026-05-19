package com.condolives.api.repository.Insight;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.condolives.api.entity.Insight.Insight;

@Repository
public interface InsightRepository extends JpaRepository<Insight, UUID> {
    List<Insight> findByGenerationId(UUID generationId);
}
