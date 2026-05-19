package com.condolives.api.repository.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.enums.MemberRole;

public interface CondoMemberRepository extends JpaRepository<CondoMember, UUID> {

    Optional<CondoMember> findByCondominiumIdAndUserEmail(UUID condominiumId, String email);

    List<CondoMember> findAllByCondominiumIdAndRole(UUID condominiumId, MemberRole role);

    List<CondoMember> findAllByCondominiumIdAndRoleIn(UUID condominiumId, List<MemberRole> roles);

    Optional<CondoMember> findFirstByUserEmail(String email);

    List<CondoMember> findAllByCondominiumId(UUID condominiumId);

    boolean existsByUserIdAndCondominiumId(UUID userId, UUID condominiumId);

    Optional<CondoMember> findByIdAndCondominiumId(UUID memberId, UUID condominiumId);

    long countByCondominiumId(UUID condominiumId);

    long countByCondominiumIdAndRoleNotIn(UUID condominiumId, List<MemberRole> excludedRoles);

    @Query("SELECT COUNT(m) FROM CondoMember m WHERE m.condominiumId = :cid AND m.unit IS NOT NULL AND (LOWER(m.unit.identifier) LIKE LOWER(CONCAT('%', :pattern, '%')) OR LOWER(COALESCE(m.unit.block, '')) LIKE LOWER(CONCAT('%', :pattern, '%')) OR LOWER(COALESCE(m.unit.street, '')) LIKE LOWER(CONCAT('%', :pattern, '%')))")
    long countByCondominiumIdAndUnitAddressContaining(@Param("cid") UUID cid, @Param("pattern") String pattern);
}
