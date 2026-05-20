package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.like.LikeResponse;

public interface LikeService {
    void createOrDelete(UUID postId, UUID residentId);
    void delete(UUID postId, UUID residentId);
    List<LikeResponse> listMyLikes(UUID residentId);
}
