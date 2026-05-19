package com.condolives.api.dto.post.Trade;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.member.MemberNameResponse;
import com.condolives.api.entity.Post.Trade.Trade;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.entity.User.UserAccount;
import com.condolives.api.enums.ItemType;
import com.condolives.api.enums.MemberRole;
import com.condolives.api.enums.TradeStatus;
import com.condolives.api.enums.TradeType;

public record TradeAdminDetailResponse(
        UUID id,
        UUID condominiumId,
        UUID memberId,
        UUID guardianId,
        String memberName,
        String guardianName,
        MemberRole role,
        String memberUnitAddress,
        String memberAvatarUrl,
        String memberEmail,
        String memberPhone,
        List<MemberNameResponse> dependents,
        String title,
        String description,
        TradeType tradeType,
        String tradeTypeDescricao,
        ItemType itemType,
        String itemTypeDescricao,
        TradeStatus status,
        String statusDescricao,
        Instant joinedAt,
        Instant createdAt) {

    public static TradeAdminDetailResponse from(Trade t) {
        CondoMember m = t.getMember();
        CondoMember g = m.getGuardian();
        UserAccount u = m.getUser();

        return new TradeAdminDetailResponse(
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
                m.getDependents().stream().map(MemberNameResponse::from).toList(),
                t.getTitle(),
                t.getDescription(),
                t.getTradeType(),
                t.getTradeType().getDescricao(),
                t.getItemType(),
                t.getItemType().getDescricao(),
                t.getStatus(),
                t.getStatus().getDescricao(),
                u.getCreatedAt(),
                t.getCreatedAt());
    }
}
