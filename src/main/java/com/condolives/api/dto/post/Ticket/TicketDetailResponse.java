package com.condolives.api.dto.post.Ticket;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.condolives.api.entity.Post.Comment;
import com.condolives.api.entity.Post.Ticket.Ticket;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.enums.PostStatus;

public record TicketDetailResponse(
                UUID id,
                UUID condominiumId,
                UUID residentId,
                String residentName,
                String residentUnitAddress,
                String residentAvatarUrl,
                boolean showName,
                String title,
                String description,
                String location,
                PostStatus status,
                String statusDescricao,
                List<CategoryDto> categories,
                List<String> imageUrls,
                long likeCount,
                List<CommentDto> comments,
                Instant createdAt) {

        public record CategoryDto(UUID id, String name) {
        }

        public record CommentDto(UUID id, UUID residentId, String content, Instant createdAt,
                        String creatorName, String creatorUnit) {
        }

        public static TicketDetailResponse from(Ticket t, long likeCount, List<Comment> comments,
                        Map<UUID, CondoMember> memberMap) {
                List<CommentDto> active = comments.stream()
                                .filter(c -> c.getDeletedAt() == null)
                                .map(c -> {
                                        CondoMember cm = memberMap.get(c.getMemberId());
                                        return new CommentDto(
                                                        c.getId(),
                                                        c.getMemberId(),
                                                        c.getContent(),
                                                        c.getCreatedAt(),
                                                        cm != null ? cm.getUser().getName() : null,
                                                        cm != null ? cm.getUnitAddress() : null);
                                })
                                .toList();

                boolean show = t.isShowName();
                return new TicketDetailResponse(
                                t.getId(),
                                t.getCondominiumId(),
                                t.getMemberId(),
                                show && t.getMember() != null ? t.getMember().getUser().getName() : null,
                                show && t.getMember() != null ? t.getMember().getUnitAddress() : null,
                                show && t.getMember() != null ? t.getMember().getUser().getAvatarUrl() : null,
                                show,
                                t.getTitle(),
                                t.getDescription(),
                                t.getLocation(),
                                t.getStatus(),
                                t.getStatus().getDescricao(),
                                t.getCategories().stream()
                                                .map(c -> new CategoryDto(c.getId(), c.getName()))
                                                .toList(),
                                t.getImages().stream()
                                                .map(img -> img.getUrl())
                                                .toList(),
                                likeCount,
                                active,
                                t.getCreatedAt());
        }
}
