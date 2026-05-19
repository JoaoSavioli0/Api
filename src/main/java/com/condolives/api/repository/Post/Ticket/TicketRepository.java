package com.condolives.api.repository.Post.Ticket;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.condolives.api.entity.Post.Ticket.Ticket;
import com.condolives.api.enums.PostStatus;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findAllByMemberIdAndCondominiumId(UUID memberId, UUID condominiumId);

    @EntityGraph(attributePaths = { "member", "categories" })
    Optional<Ticket> findWithMemberByIdAndCondominiumId(UUID id, UUID condominiumId);

    @EntityGraph(attributePaths = "member")
    Optional<Ticket> findByIdAndCondominiumId(UUID id, UUID condominiumId);

    @EntityGraph(attributePaths = { "member", "categories" })
    List<Ticket> findAllByCondominiumId(UUID condominiumId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.condominiumId = :cid AND t.status = :status")
    long countByCondominiumIdAndStatus(@Param("cid") UUID cid, @Param("status") PostStatus status);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.condominiumId = :cid AND t.createdAt BETWEEN :start AND :end")
    long countByCondominiumIdInPeriod(@Param("cid") UUID cid, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.condominiumId = :cid AND t.status = :status AND t.createdAt BETWEEN :start AND :end")
    long countByCondominiumIdAndStatusInPeriod(@Param("cid") UUID cid, @Param("status") PostStatus status, @Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT t FROM Ticket t WHERE t.condominiumId = :cid AND t.createdAt BETWEEN :start AND :end ORDER BY t.createdAt DESC")
    List<Ticket> findByCondominiumIdInPeriod(@Param("cid") UUID cid, @Param("start") Instant start, @Param("end") Instant end);
}