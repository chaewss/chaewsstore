package com.chaewsstore.core.domain.bid.dto;

import com.querydsl.core.annotations.QueryProjection;

public record ReadProductBidQueryDto(
    Integer bidPrice,
    Long quantity
) {

    @QueryProjection
    public ReadProductBidQueryDto {
    }
}
