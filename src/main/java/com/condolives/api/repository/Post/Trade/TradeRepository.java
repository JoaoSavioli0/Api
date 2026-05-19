package com.condolives.api.repository.Post.Trade;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.Post.Trade.Trade;

public interface TradeRepository extends JpaRepository<Trade, UUID> {

    @EntityGraph(attributePaths = "member")
    List<Trade> findAllByCondominiumId(UUID condominiumId);

    @EntityGraph(attributePaths = "member")
    Optional<Trade> findWithMemberByIdAndCondominiumId(UUID id, UUID condominiumId);

    Optional<Trade> findByIdAndCondominiumId(UUID id, UUID condominiumId);
}
