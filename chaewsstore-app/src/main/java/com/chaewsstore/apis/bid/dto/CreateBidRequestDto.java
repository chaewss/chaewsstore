package com.chaewsstore.apis.bid.dto;

import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.product.Product;

public record CreateBidRequestDto(
    Integer price
) {

    public Bid toEntity(Product product, Account account) {
        return Bid.create(price, product, account);
    }
}
