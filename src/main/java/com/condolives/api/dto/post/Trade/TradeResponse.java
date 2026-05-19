package com.condolives.api.dto.post.Trade;

import java.time.Instant;
import java.util.UUID;

import com.condolives.api.entity.Post.Trade.Trade;
import com.condolives.api.enums.ItemType;
import com.condolives.api.enums.TradeStatus;
import com.condolives.api.enums.TradeType;

public record TradeResponse(
        UUID id,
        UUID condominiumId,
        UUID memberId,
        String title,
        String description,
        TradeType tradeType,
        String tradeTypeDescricao,
        ItemType itemType,
        String itemTypeDescricao,
        TradeStatus status,
        String statusDescricao,
        Instant createdAt) {

    public static TradeResponse from(Trade t) {
        return new TradeResponse(
                t.getId(),
                t.getCondominiumId(),
                t.getMemberId(),
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
