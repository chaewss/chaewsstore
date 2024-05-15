package com.chaewsstore.dto.bid;

import com.chaewsstore.entity.Account;
import com.chaewsstore.entity.Bid;
import com.chaewsstore.entity.Product;

public record CreateBidRequestDto(
    Integer price
) {

    public Bid toEntity(Product product, Account account) {
        return Bid.create(price, product, account);
    }
}
