package com.chaewsstore.repository;


import static com.chaewsstore.entity.QBid.bid;

import com.chaewsstore.dto.QReadProductBidQueryDto;
import com.chaewsstore.dto.ReadProductBidQueryDto;
import com.chaewsstore.entity.Bid;
import com.chaewsstore.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BidCustomRepositoryImpl implements BidCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<ReadProductBidQueryDto> findAllByProduct(Product product, Pageable pageable) {
        return queryFactory.select(new QReadProductBidQueryDto(
            bid.price,
            bid.count().as("quantity")))
            .from(bid)
            .where(bid.product.eq(product))
            .groupBy(bid.price)
            .orderBy(bid.price.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
