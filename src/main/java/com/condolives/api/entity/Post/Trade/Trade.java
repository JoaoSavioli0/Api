package com.condolives.api.entity.Post.Trade;

import com.condolives.api.converter.ItemTypeConverter;
import com.condolives.api.converter.TradeStatusConverter;
import com.condolives.api.converter.TradeTypeConverter;
import com.condolives.api.entity.Post.Post;
import com.condolives.api.enums.ItemType;
import com.condolives.api.enums.TradeStatus;
import com.condolives.api.enums.TradeType;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "trade")
@DiscriminatorValue("trade")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade extends Post {

    private String title;

    private String description;

    @Convert(converter = TradeTypeConverter.class)
    @Column(name = "trade_type", nullable = false)
    private TradeType tradeType;

    @Convert(converter = ItemTypeConverter.class)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Setter
    @Convert(converter = TradeStatusConverter.class)
    @Column(nullable = false)
    private TradeStatus status;
}
