package com.chaewsstore.core.domain.bid;

import static com.chaewsstore.core.domain.bid.QBid.bid;

import com.chaewsstore.core.domain.bid.dto.QReadProductBidQueryDto;
import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.core.domain.product.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class BidCustomRepositoryImpl implements BidCustomRepository {

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
