package com.condolives.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.condolives.api.controller.helpers.AuthHelper;
import com.condolives.api.dto.comment.CreateCommentRequest;
import com.condolives.api.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody @Valid CreateCommentRequest request,
            Authentication authentication) {

        UUID residentId = UUID.fromString((String) authentication.getPrincipal());

        commentService.create(request, residentId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID residentId = UUID.fromString((String) authentication.getPrincipal());
        UUID condominiumId = AuthHelper.condominiumId(authentication);
        boolean isAdmin = AuthHelper.isAdmin(authentication);

        System.out.println("Resident ID: " + residentId);
        System.out.println("Is Admin: " + isAdmin);

        commentService.delete(id, condominiumId, residentId, isAdmin);

        return ResponseEntity.ok().build();
    }

    // @GetMapping("/my")
    // public ResponseEntity<List<CommentResponse>> myComments(Authentication
    // authentication) {
    // UUID residentId = UUID.fromString((String) authentication.getPrincipal());
    // UUID condominiumId = PostHelper.condominiumId(authentication);

    // return ResponseEntity.ok(commentService.listMyComments(residentId,
    // condominiumId));
    // }
}
