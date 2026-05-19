package com.condolives.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.dto.like.CreateLikeRequest;
import com.condolives.api.dto.like.LikeResponse;
import com.condolives.api.service.LikeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody @Valid CreateLikeRequest request,
            Authentication authentication) {

        UUID residentId = UUID.fromString((String) authentication.getPrincipal());

        likeService.createOrDelete(request.getPostId(), residentId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID postId,
            Authentication authentication) {

        UUID residentId = UUID.fromString((String) authentication.getPrincipal());

        likeService.delete(postId, residentId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<LikeResponse>> myLikes(Authentication authentication) {
        UUID residentId = UUID.fromString((String) authentication.getPrincipal());

        return ResponseEntity.ok(likeService.listMyLikes(residentId));
    }
}
