package com.condolives.api.repository.Post;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.condolives.api.entity.Post.Post;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Modifying
    @Query("UPDATE Post p SET p.visible = false WHERE p.memberId = :memberId AND p.condominiumId = :condominiumId")
    void hideAllByMemberIdAndCondominiumId(@Param("memberId") UUID memberId, @Param("condominiumId") UUID condominiumId);
}
