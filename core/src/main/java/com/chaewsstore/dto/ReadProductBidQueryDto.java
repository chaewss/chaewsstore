package com.chaewsstore.dto;

import com.querydsl.core.annotations.QueryProjection;

public record ReadProductBidQueryDto(
    Integer bidPrice,
    Long quantity
) {

    @QueryProjection
    public ReadProductBidQueryDto(Integer bidPrice, Long quantity) {
        this.bidPrice = bidPrice;
        this.quantity = quantity;
    }
}
