package com.condolives.api.dto.post.Trade;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.entity.Post.Trade.Trade;
import com.condolives.api.enums.ItemType;
import com.condolives.api.enums.TradeStatus;
import com.condolives.api.enums.TradeType;

public record TradeListResponse(
        UUID id,
        UUID memberId,
        String memberName,
        String memberUnitAddress,
        String memberAvatarUrl,
        String title,
        String description,
        TradeType tradeType,
        String tradeTypeDescricao,
        ItemType itemType,
        String itemTypeDescricao,
        TradeStatus status,
        String statusDescricao,
        Instant createdAt) {

    public static TradeListResponse from(Trade t) {
        return new TradeListResponse(
                t.getId(),
                t.getMemberId(),
                t.getMember().getUser().getName(),
                t.getMember().getUnitAddress(),
                t.getMember().getUser().getAvatarUrl(),
                t.getTitle(),
                t.getDescription(),
                t.getTradeType(),
                t.getTradeType().getDescricao(),
                t.getItemType(),
                t.getItemType().getDescricao(),
                t.getStatus(),
                t.getStatus().getDescricao(),
                t.getCreatedAt());
    }
}
