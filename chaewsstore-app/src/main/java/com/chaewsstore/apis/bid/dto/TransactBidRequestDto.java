package com.chaewsstore.apis.bid.dto;

public record TransactBidRequestDto(
    Long productId,
    Integer price
) {

}
