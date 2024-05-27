package com.chaewsstore.core.domain.bid;

import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long>, BidCustomRepository {

    boolean existsByProductAndBidder(Product product, Account bidder);
}
