package com.condolives.api.service;

import java.util.List;
import java.util.UUID;

import com.condolives.api.dto.post.Trade.CreateTradeRequest;
import com.condolives.api.dto.post.Trade.TradeAdminDetailResponse;
import com.condolives.api.dto.post.Trade.TradeListResponse;
import com.condolives.api.dto.post.Trade.TradeResponse;

public interface TradeService {
    TradeResponse createTrade(CreateTradeRequest request, UUID residentId, UUID condominiumId);
    List<TradeListResponse> listTrades(UUID condominiumId);
    TradeAdminDetailResponse getTradeAdmin(UUID tradeId, UUID condominiumId);
    void deleteTrade(UUID tradeId, UUID memberId, UUID condominiumId, boolean isAdmin);
    void updateStatus(UUID tradeId, String statusStr, UUID condominiumId);
}
