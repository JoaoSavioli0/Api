package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.notice.CreateNoticeRequest;
import com.condolives.api.dto.notice.NoticeResponse;
import com.condolives.api.dto.notice.UnreadNoticeResponse;

public interface NoticeService {
    List<NoticeResponse> list(UUID condominiumId);
    NoticeResponse create(CreateNoticeRequest request, UUID condominiumId, UUID memberId);
    NoticeResponse update(UUID id, CreateNoticeRequest request, UUID condominiumId);
    void delete(UUID id, UUID condominiumId);
    List<UnreadNoticeResponse> listUnread(UUID memberId, UUID condominiumId);
    void markAsRead(UUID noticeId, UUID memberId, UUID condominiumId);
    List<UnreadNoticeResponse> listAllForMember(UUID memberId, UUID condominiumId);
    List<String> getTargetOptions(UUID condominiumId);
}
