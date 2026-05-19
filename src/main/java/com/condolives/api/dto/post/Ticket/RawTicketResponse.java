package com.condolives.api.dto.post.Ticket;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.condolives.api.entity.Post.Ticket.Ticket;
import com.condolives.api.enums.PostStatus;

public record RawTicketResponse(
        UUID id,
        UUID condominiumId,
        UUID memberId,
        String title,
        String description,
        String location,
        PostStatus status,
        String statusDescricao,
        List<CategoryDto> categories,
        List<String> imageUrls,
        Instant createdAt) {

    public record CategoryDto(UUID id, String name) {
    }

    public static RawTicketResponse from(Ticket t, List<String> imageUrls) {
        return new RawTicketResponse(
                t.getId(),
                t.getCondominiumId(),
                t.getMemberId(),
                t.getTitle(),
                t.getDescription(),
                t.getLocation(),
                t.getStatus(),
                t.getStatus().getDescricao(),
                t.getCategories().stream()
                        .map(c -> new CategoryDto(c.getId(), c.getName()))
                        .toList(),
                imageUrls,
                t.getCreatedAt());
    }
}
