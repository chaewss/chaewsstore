package com.chaewsstore.dto.bid;

import com.chaewsstore.dto.ReadProductBidQueryDto;

public record ReadProductBidResponseDto(
    Integer bidPrice,
    Long quantity
) {

    public static ReadProductBidResponseDto from(ReadProductBidQueryDto readProductBidQueryDto) {
        return new ReadProductBidResponseDto(readProductBidQueryDto.bidPrice(),
            readProductBidQueryDto.quantity());
    }
}
