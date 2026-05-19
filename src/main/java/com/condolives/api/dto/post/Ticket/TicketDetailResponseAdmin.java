package com.condolives.api.dto.post.Ticket;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.member.MemberNameResponse;
import com.condolives.api.entity.Post.Comment;
import com.condolives.api.entity.Post.Ticket.Ticket;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.entity.User.UserAccount;
import com.condolives.api.enums.MemberRole;
import com.condolives.api.enums.PostStatus;

public record TicketDetailResponseAdmin(
                UUID id,
                UUID condominiumId,
                UUID residentId,
                UUID guardianId,
                String residentName,
                String guardianName,
                MemberRole role,
                String residentUnitAddress,
                String residentAvatarUrl,
                String residentEmail,
                String residentPhone,
                List<MemberNameResponse> dependents,
                String title,
                String description,
                String location,
                PostStatus status,
                String statusDescricao,
                List<CategoryDto> categories,
                List<String> imageUrls,
                long likeCount,
                Instant joinedAt,
                List<CommentDto> comments,
                List<HiddenCommentDto> hiddenComments,
                boolean showName,
                Instant createdAt) {

        public record CategoryDto(UUID id, String name) {
        }

        public record CommentDto(UUID id, UUID residentId, String content, Instant createdAt) {
        }

        public record HiddenCommentDto(UUID id, UUID residentId, String content, Instant createdAt,
                        Instant deletedAt, UUID deletedBy) {
        }

        public static TicketDetailResponseAdmin from(Ticket t, long likeCount, List<Comment> comments) {
                CondoMember g = t.getMember().getGuardian();
                UserAccount u = t.getMember().getUser();
                CondoMember m = t.getMember();

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

                return new TicketDetailResponseAdmin(
                                t.getId(),
                                t.getCondominiumId(),
                                t.getMemberId(),
                                m.getGuardianId(),
                                u.getName(),
                                g != null ? g.getUser().getName() : "",
                                m.getRole(),
                                m.getUnitAddress(),
                                u.getAvatarUrl(),
                                u.getEmail(),
                                u.getPhone(),
                                m.getDependents().stream()
                                                .map(MemberNameResponse::from)
                                                .toList(),
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
                                u.getCreatedAt(),
                                active,
                                hidden,
                                t.isShowName(),
                                t.getCreatedAt());
        }
}
