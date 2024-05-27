package com.chaewsstore.core.domain.bid;

import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.core.domain.product.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BidCustomRepository {

    List<ReadProductBidQueryDto> findAllByProduct(Product product, Pageable pageable);
}
