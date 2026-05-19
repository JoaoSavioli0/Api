package com.condolives.api.repository.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.User.MemberInvite;

public interface MemberInviteRepository extends JpaRepository<MemberInvite, UUID> {

    Optional<MemberInvite> findByToken(String token);

    @EntityGraph(attributePaths = "member")
    List<MemberInvite> findAllByMemberCondominiumIdOrderByCreatedAtDesc(UUID condominiumId);
}
