package com.chaewsstore.core.domain.bid;

import static com.chaewsstore.core.domain.bid.QBid.bid;

import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.bid.dto.QReadProductBidQueryDto;
import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.product.Product;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class BidCustomRepositoryImpl implements BidCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<ReadProductBidQueryDto> findAllByProduct(Product product, BidType bidType,
        Pageable pageable) {
        if (bidType == null) {
            return queryFactory.select(new QReadProductBidQueryDto(
                    bid.price,
                    bid.transactionAt
                ))
                .from(bid)
                .where(
                    bid.product.eq(product),
                    bid.transactionAt.isNotNull())
                .groupBy(bid.transactionAt)
                .orderBy(bid.transactionAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        } else {
            OrderSpecifier<?> orderSpecifier =
                bidType == BidType.BUY ? bid.price.desc() : bid.price.asc();

            return queryFactory.select(new QReadProductBidQueryDto(
                    bid.price,
                    bid.count().as("quantity")
                ))
                .from(bid)
                .where(
                    bid.product.eq(product),
                    bid.bidType.eq(bidType),
                    bid.status.eq(Status.LIVE))
                .groupBy(bid.price)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        }
    }
}
