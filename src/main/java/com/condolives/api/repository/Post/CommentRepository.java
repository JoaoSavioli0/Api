package com.condolives.api.repository.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.condolives.api.entity.Post.Comment;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findAllByPostId(UUID postId);

    Optional<Comment> findByIdAndMemberId(UUID id, UUID memberId);

    @Query("""
            SELECT c FROM Comment c
            JOIN Post p ON p.id = c.postId
            WHERE c.id = :id AND p.condominiumId = :condominiumId
            """)
    Optional<Comment> findByIdAndCondominiumId(@Param("id") UUID id, @Param("condominiumId") UUID condominiumId);

    @Query("""
            SELECT c FROM Comment c
            JOIN Post p ON p.id = c.postId
            WHERE c.memberId = :memberId AND p.condominiumId = :condominiumId
            """)
    List<Comment> findAllByMemberIdAndCondominiumId(@Param("memberId") UUID memberId,
            @Param("condominiumId") UUID condominiumId);


    @Query("SELECT c.postId, COUNT(c) FROM Comment c WHERE c.postId IN :postIds GROUP BY c.postId")
    List<Object[]> countByPostIdIn(List<UUID> postIds);
}