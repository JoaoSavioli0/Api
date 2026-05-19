package com.condolives.api.dto.member;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.condolives.api.dto.post.Ticket.TicketResponse;
import com.condolives.api.entity.Post.Comment;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.entity.User.UserAccount;
import com.condolives.api.enums.MemberRole;

public record MemberDetailResponseAdmin(
        UUID id,
        UUID condominiumId,
        UUID guardianId,
        String name,
        String guardianName,
        MemberRole role,
        UUID unitId,
        String unitAddress,
        String avatarUrl,
        String email,
        String phone,
        List<MemberNameResponse> dependents,
        Instant joinedAt,
        List<TicketResponse> ticket,
        List<CommentDto> comments,
        List<HiddenCommentDto> hiddenComments,
        Boolean active) {

    public record CategoryDto(UUID id, String name) {
    }

    public record CommentDto(UUID id, UUID memberId, String content, Instant createdAt) {
    }

    public record HiddenCommentDto(UUID id, UUID memberId, String content, Instant createdAt,
            Instant deletedAt, UUID deletedBy) {
    }

    public static MemberDetailResponseAdmin from(CondoMember m, List<Comment> comments, List<TicketResponse> tickets,
            Map<UUID, Long> commentCounts, Map<UUID, Long> likeCounts) {
        CondoMember g = m.getGuardian();
        UserAccount u = m.getUser();

        List<CommentDto> active = comments.stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(c -> new CommentDto(c.getId(), c.getMemberId(),
                        c.getContent(), c.getCreatedAt()))
                .toList();

        List<HiddenCommentDto> hidden = comments.stream()
                .filter(c -> c.getDeletedAt() != null)
                .map(c -> new HiddenCommentDto(c.getId(), c.getMemberId(),
                        c.getContent(), c.getCreatedAt(),
                        c.getDeletedAt(), c.getDeletedBy()))
                .toList();

        return new MemberDetailResponseAdmin(
                m.getId(),
                m.getCondominiumId(),
                m.getGuardianId(),
                u.getName(),
                g != null ? g.getUser().getName() : "",
                m.getRole(),
                m.getUnitId(),
                m.getUnitAddress(),
                u.getAvatarUrl(),
                u.getEmail(),
                u.getPhone(),
                m.getDependents().stream()
                        .map(MemberNameResponse::from)
                        .toList(),
                u.getCreatedAt(),
                tickets,
                active,
                hidden,
                m.getActive());
    }
}
