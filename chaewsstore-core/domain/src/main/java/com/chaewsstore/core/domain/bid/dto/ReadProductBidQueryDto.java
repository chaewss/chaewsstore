package com.chaewsstore.core.domain.bid.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record ReadProductBidQueryDto(
    Integer bidPrice,
    Long quantity,
    LocalDateTime transactionAt
) {

    @QueryProjection
    public ReadProductBidQueryDto(Integer bidPrice, Long quantity) {
        this(bidPrice, quantity, null);
    }

    @QueryProjection
    public ReadProductBidQueryDto(Integer bidPrice, LocalDateTime transactionAt) {
        this(bidPrice, null, transactionAt);
    }
}
