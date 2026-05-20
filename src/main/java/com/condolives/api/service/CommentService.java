package com.condolives.api.service;

import java.util.UUID;

import com.condolives.api.dto.comment.CreateCommentRequest;

public interface CommentService {
    void create(CreateCommentRequest request, UUID residentId);
    void delete(UUID commentId, UUID condominiumId, UUID residentId, boolean isAdmin);
}
