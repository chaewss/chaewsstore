package com.chaewsstore.core.domain.bid;

import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.core.domain.product.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;

interface BidCustomRepository {

    List<ReadProductBidQueryDto> findAllByProduct(Product product, BidType bidType, Pageable pageable);
}
