package com.condolives.api.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.condolives.api.dto.comment.CreateCommentRequest;
import com.condolives.api.entity.Post.Comment;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Post.CommentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public void create(CreateCommentRequest request, UUID residentId) {
        try {
            commentRepository.save(Comment.builder()
                    .content(request.getContent())
                    .postId(request.getPostId())
                    .memberId(residentId)
                    .build());
        } catch (Exception e) {
            throw new ServiceException("Erro ao criar comentário", 500);
        }
    }

    @Transactional
    public void delete(UUID commentId, UUID condominiumId, UUID residentId, boolean isAdmin) {
        try {
            Comment comment = commentRepository.findByIdAndCondominiumId(commentId, condominiumId)
                    .orElseThrow(() -> new ServiceException(
                            "Comentário não encontrado ou você não tem permissão para deletar", 404));

            if (comment.getDeletedAt() != null) {
                throw new ServiceException("Comentário já foi deletado", 404);
            }

            if (comment.getMemberId().equals(residentId) || isAdmin) {
                comment.setDeletedAt(Instant.now());
                comment.setDeletedBy(residentId);
            } else {
                throw new ServiceException("Você não tem permissão para deletar este comentário", 403);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erro ao deletar comentário", 500);
        }
    }
}
