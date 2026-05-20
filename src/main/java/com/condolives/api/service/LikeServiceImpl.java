package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.condolives.api.dto.like.LikeResponse;
import com.condolives.api.entity.Post.Like;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Post.LikeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;

    public void createOrDelete(UUID postId, UUID residentId) {
        try {
            if (likeRepository.existsByMemberIdAndPostId(residentId, postId)) {
                delete(postId, residentId);
                return;
            }

            likeRepository.save(Like.builder()
                    .postId(postId)
                    .memberId(residentId)
                    .build());
        } catch (Exception e) {
            throw new ServiceException("Erro ao criar curtida", 500);
        }
    }

    @Transactional
    public void delete(UUID postId, UUID residentId) {
        try {
            Like like = likeRepository.findByMemberIdAndPostId(residentId, postId)
                    .orElseThrow(() -> new ServiceException(
                            "Curtida não encontrada ou você não tem permissão para deletar", 404));

            likeRepository.delete(like);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erro ao deletar curtida", 500);
        }
    }

    public List<LikeResponse> listMyLikes(UUID residentId) {
        try {
            return likeRepository.findAllByMemberId(residentId).stream().map(LikeResponse::from).toList();
        } catch (Exception e) {
            throw new ServiceException("Erro ao listar curtidas", 500);
        }
    }
}
