package com.condolives.api.dto.post.Ticket;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.condolives.api.entity.Post.Ticket.Ticket;
import com.condolives.api.enums.PostStatus;

public record TicketResponse(
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
                long commentCount,
                long likeCount,
                Instant createdAt) {

        public record CategoryDto(UUID id, String name) {
        }

        public static TicketResponse from(Ticket t, long commentCount, long likeCount) {
                boolean show = t.isShowName();
                return new TicketResponse(
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
                                commentCount,
                                likeCount,
                                t.getCreatedAt());
        }
}
