package com.chaewsstore.repository;

import com.chaewsstore.dto.ReadProductBidQueryDto;
import com.chaewsstore.entity.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BidCustomRepository {

    List<ReadProductBidQueryDto> findAllByProduct(Product product, Pageable pageable);
}
