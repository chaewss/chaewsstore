package com.chaewsstore.core.domain;

import static com.chaewsstore.core.domain.ProductFixture.PRODUCT1;
import static com.chaewsstore.core.domain.UserFixture.ANOTHER_USER;
import static com.chaewsstore.core.domain.UserFixture.BUYER;
import static com.chaewsstore.core.domain.UserFixture.SELLER;
import static com.chaewsstore.core.domain.UserFixture.USER;

import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.user.User;
import java.time.LocalDateTime;

public enum BidFixture {

    BID(1L, null, PRODUCT1.getProduct(), null, null, null, null,
        null, false),

    USER_BID(2L, 800, PRODUCT1.getProduct(), USER.getUser(), null, null, null,
        null, false),
    ANOTHER_USER_BID(3L, 800, PRODUCT1.getProduct(), ANOTHER_USER.getUser(), null, null, null,
        null, false),

    BUY_BID_LIVE(4L, 800, PRODUCT1.getProduct(), BUYER.getUser(), Status.LIVE, BidType.BUY, null,
        null, false),
    SELL_BID_LIVE(5L, 800, PRODUCT1.getProduct(), SELLER.getUser(), Status.LIVE, BidType.SELL, null,
        null, false),

    SELL_BID_AUTHENTICATED(6L, 800, PRODUCT1.getProduct(), SELLER.getUser(), Status.AUTHENTICATED,
        BidType.SELL, null, null, false),

    BUY_BID_IN_TRANSACTION(7L, 700, PRODUCT1.getProduct(), BUYER.getUser(), Status.IN_TRANSACTION,
        BidType.BUY, null, LocalDateTime.of(2024, 8, 4, 1, 5), false),
    SELL_BID_IN_TRANSACTION(8L, 700, PRODUCT1.getProduct(), SELLER.getUser(), Status.IN_TRANSACTION,
        BidType.SELL, null, LocalDateTime.of(2024, 8, 4, 1, 5), false),
    ;

    private final Long id;
    private final Integer price;
    private final Product product;
    private final User bidder;
    private final Status status;
    private final BidType bidType;
    private final Bid relatedBid;
    private final LocalDateTime transactionAt;
    private final Boolean isDeleted;


    BidFixture(Long id, Integer price, Product product, User bidder, Status status, BidType bidType,
        Bid relatedBid, LocalDateTime transactionAt, Boolean isDeleted) {
        this.id = id;
        this.price = price;
        this.product = product;
        this.bidder = bidder;
        this.status = status;
        this.bidType = bidType;
        this.relatedBid = relatedBid;
        this.transactionAt = transactionAt;
        this.isDeleted = isDeleted;
    }

    public Bid getBid() {
        return Bid.builder()
            .id(id)
            .price(price)
            .product(product)
            .bidder(bidder)
            .status(status)
            .bidType(bidType)
            .relatedBid(relatedBid)
            .transactionAt(transactionAt)
            .isDeleted(isDeleted)
            .build();
    }

    public Bid getBidWithStatus(Status status) {
        return Bid.builder()
            .id(id)
            .price(price)
            .product(product)
            .bidder(bidder)
            .status(status)
            .bidType(bidType)
            .relatedBid(relatedBid)
            .transactionAt(transactionAt)
            .isDeleted(isDeleted)
            .build();
    }

    public Bid getBidWithUserAndProductAndPriceAndBidTypeAndStatus(User bidder, Product product,
        Integer price, BidType bidType, Status status) {
        return Bid.builder()
            .id(id)
            .price(price)
            .product(product)
            .bidder(bidder)
            .status(status)
            .bidType(bidType)
            .relatedBid(relatedBid)
            .transactionAt(transactionAt)
            .isDeleted(isDeleted)
            .build();
    }

    public Bid getBidWithProductAndPriceAndBidTypeAndStatusAndTransactionAt(Product product,
        Integer price, BidType bidType, Status status, LocalDateTime transactionAt) {
        return Bid.builder()
            .id(id)
            .price(price)
            .product(product)
            .bidder(bidder)
            .status(status)
            .bidType(bidType)
            .relatedBid(relatedBid)
            .transactionAt(transactionAt)
            .isDeleted(isDeleted)
            .build();
    }
}
