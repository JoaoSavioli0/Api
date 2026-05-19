package com.condolives.api.repository.Post;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.condolives.api.entity.Post.NoticeRead;

@Repository
public interface NoticeReadRepository extends JpaRepository<NoticeRead, UUID> {

    long countByNoticeId(UUID noticeId);

    boolean existsByNoticeIdAndMemberId(UUID noticeId, UUID memberId);

    Optional<NoticeRead> findByNoticeIdAndMemberId(UUID noticeId, UUID memberId);
}
