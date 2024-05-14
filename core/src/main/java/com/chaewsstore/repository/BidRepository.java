package com.chaewsstore.repository;

import com.chaewsstore.entity.Account;
import com.chaewsstore.entity.Bid;
import com.chaewsstore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {

    boolean existsByProductAndBidder(Product product, Account bidder);
}
