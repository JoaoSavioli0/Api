package com.condolives.api.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.dto.post.Trade.CreateTradeRequest;
import com.condolives.api.dto.post.Trade.TradeAdminDetailResponse;
import com.condolives.api.dto.post.Trade.TradeListResponse;
import com.condolives.api.dto.post.Trade.TradeResponse;
import com.condolives.api.entity.Post.Trade.Trade;
import com.condolives.api.enums.ItemType;
import com.condolives.api.enums.TradeStatus;
import com.condolives.api.enums.TradeType;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Post.Trade.TradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;

    @Transactional
    public TradeResponse createTrade(CreateTradeRequest request, UUID residentId, UUID condominiumId) {
        TradeType tradeType = parseTradeType(request.tradeType());
        ItemType itemType = parseItemType(request.itemType());

        Trade trade = Trade.builder()
                .condominiumId(condominiumId)
                .memberId(residentId)
                .visible(true)
                .title(request.title())
                .description(request.description())
                .tradeType(tradeType)
                .itemType(itemType)
                .status(TradeStatus.ABERTO)
                .build();

        return TradeResponse.from(tradeRepository.save(trade));
    }

    @Transactional(readOnly = true)
    public List<TradeListResponse> listTrades(UUID condominiumId) {
        return tradeRepository.findAllByCondominiumId(condominiumId).stream()
                .map(TradeListResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TradeAdminDetailResponse getTradeAdmin(UUID tradeId, UUID condominiumId) {
        Trade trade = tradeRepository.findWithMemberByIdAndCondominiumId(tradeId, condominiumId)
                .orElseThrow(() -> new ServiceException("Troca não encontrada", 404));

        return TradeAdminDetailResponse.from(trade);
    }

    @Transactional
    public void deleteTrade(UUID tradeId, UUID memberId, UUID condominiumId, boolean isAdmin) {
        Objects.requireNonNull(condominiumId, "condominiumId is null");
        Objects.requireNonNull(memberId, "memberId is null");

        Trade trade = tradeRepository.findByIdAndCondominiumId(tradeId, condominiumId)
                .orElseThrow(() -> new ServiceException("Troca não encontrada", 404));

        if (!trade.getMemberId().equals(memberId) && !isAdmin) {
            throw new ServiceException("Usuário não tem permissão", 403);
        }

        trade.setVisible(false);
        tradeRepository.save(trade);
    }

    @Transactional
    public void updateStatus(UUID tradeId, String statusStr, UUID condominiumId) {
        Trade trade = tradeRepository.findByIdAndCondominiumId(tradeId, condominiumId)
                .orElseThrow(() -> new ServiceException("Troca não encontrada", 404));

        TradeStatus status;
        try {
            status = TradeStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Status inválido. Valores permitidos: ABERTO, FECHADO", 422);
        }

        trade.setStatus(status);
        tradeRepository.save(trade);
    }

    private TradeType parseTradeType(String s) {
        try {
            return TradeType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Tipo de troca inválido. Valores: DOACAO, TROCA, VENDA, SERVICO", 422);
        }
    }

    private ItemType parseItemType(String s) {
        try {
            return ItemType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Tipo de item inválido. Valores: PRODUTO, SERVICO", 422);
        }
    }
}
