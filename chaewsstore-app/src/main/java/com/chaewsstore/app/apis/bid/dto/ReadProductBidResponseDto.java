package com.chaewsstore.app.apis.bid.dto;

import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

public record ReadProductBidResponseDto(
    Integer bidPrice,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long quantity,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yy/MM/dd", timezone = "Asia/Seoul")
    LocalDateTime transactionAt
) {

    public static ReadProductBidResponseDto from(ReadProductBidQueryDto readProductBidQueryDto) {
        return new ReadProductBidResponseDto(readProductBidQueryDto.bidPrice(),
            readProductBidQueryDto.quantity(), readProductBidQueryDto.transactionAt());
    }
}
