package com.chaewsstore.core.domain.bid;

import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

interface BidRepository extends JpaRepository<Bid, Long>, BidCustomRepository {

    boolean existsByProductAndBidder(Product product, User bidder);
}
