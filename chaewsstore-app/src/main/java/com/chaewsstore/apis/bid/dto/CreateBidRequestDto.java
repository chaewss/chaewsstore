package com.chaewsstore.apis.bid.dto;

import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.product.Product;

public record CreateBidRequestDto(
    Integer price
) {

    public Bid toEntity(Product product, User user) {
        return Bid.create(price, product, user);
    }
}
