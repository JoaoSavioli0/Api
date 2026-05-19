package com.condolives.api.dto.like;

import java.util.UUID;

import com.condolives.api.entity.Post.Like;

public record LikeResponse(UUID id, String postId) {
    public static LikeResponse from(Like like) {
        return new LikeResponse(like.getId(), like.getPostId().toString());
    }
}