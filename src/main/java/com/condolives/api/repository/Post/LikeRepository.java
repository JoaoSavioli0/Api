package com.condolives.api.repository.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.condolives.api.entity.Post.Like;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    long countByPostId(UUID postId);

    Optional<Like> findByMemberIdAndPostId(UUID memberId, UUID postId);

    boolean existsByMemberIdAndPostId(UUID memberId, UUID postId);

    List<Like> findAllByMemberId(UUID memberId);

    @Query("SELECT l.postId, COUNT(l) FROM Like l WHERE l.postId IN :postIds GROUP BY l.postId")
    List<Object[]> countByPostIdIn(List<UUID> postIds);
}
