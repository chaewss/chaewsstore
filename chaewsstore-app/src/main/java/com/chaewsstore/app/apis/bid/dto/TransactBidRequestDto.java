package com.chaewsstore.app.apis.bid.dto;

public record TransactBidRequestDto(
    Long productId,
    Integer price
) {

}
