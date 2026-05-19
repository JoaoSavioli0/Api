package com.condolives.api.repository.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.condolives.api.entity.Post.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, UUID> {

    List<Notice> findByCondominiumIdOrderByCreatedAtDesc(UUID condominiumId);

    Optional<Notice> findByIdAndCondominiumId(UUID id, UUID condominiumId);

    @Query("""
            SELECT n FROM Notice n
            WHERE n.condominiumId = :cid
              AND n.id NOT IN (
                  SELECT nr.noticeId FROM NoticeRead nr WHERE nr.memberId = :memberId
              )
              AND (
                  n.targetType = 'all'
                  OR LOWER(n.targetValue) LIKE LOWER(CONCAT('%', :unitAddress, '%'))
                  OR LOWER(:unitAddress) LIKE LOWER(CONCAT('%', n.targetValue, '%'))
              )
            ORDER BY n.createdAt DESC
            """)
    List<Notice> findUnreadForMember(
            @Param("cid") UUID condominiumId,
            @Param("memberId") UUID memberId,
            @Param("unitAddress") String unitAddress);

    @Query("""
            SELECT n FROM Notice n
            WHERE n.condominiumId = :cid
              AND (
                  n.targetType = 'all'
                  OR LOWER(n.targetValue) LIKE LOWER(CONCAT('%', :unitAddress, '%'))
                  OR LOWER(:unitAddress) LIKE LOWER(CONCAT('%', n.targetValue, '%'))
              )
            ORDER BY n.createdAt DESC
            """)
    List<Notice> findAllForMember(
            @Param("cid") UUID condominiumId,
            @Param("unitAddress") String unitAddress);
}
