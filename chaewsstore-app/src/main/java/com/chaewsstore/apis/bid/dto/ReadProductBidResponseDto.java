package com.chaewsstore.apis.bid.dto;

import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;

public record ReadProductBidResponseDto(
    Integer bidPrice,
    Long quantity
) {

    public static ReadProductBidResponseDto from(ReadProductBidQueryDto readProductBidQueryDto) {
        return new ReadProductBidResponseDto(readProductBidQueryDto.bidPrice(),
            readProductBidQueryDto.quantity());
    }
}
