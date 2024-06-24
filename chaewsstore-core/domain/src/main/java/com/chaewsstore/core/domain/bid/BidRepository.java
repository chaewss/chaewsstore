package com.chaewsstore.core.domain.bid;

import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.user.User;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

interface BidRepository extends JpaRepository<Bid, Long>, BidCustomRepository {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Bid> findFirstByProductIdAndPriceAndBidTypeAndStatusOrderByCreatedAtAsc(Long productId,
        Integer price, BidType bidType, Status status);

    Optional<Bid> findByProductAndBidderAndStatusAndBidType(Product product, User bidder, Status status, BidType bidType);
}
