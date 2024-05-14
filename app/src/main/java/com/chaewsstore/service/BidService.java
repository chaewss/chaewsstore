package com.chaewsstore.service;

import static com.chaewsstore.exception.ExceptionConstants.DUPLICATION_BID;
import static com.chaewsstore.exception.ExceptionConstants.NOT_FOUND_PRODUCT;

import com.chaewsstore.dto.bid.CreateBidRequestDto;
import com.chaewsstore.entity.Account;
import com.chaewsstore.entity.Product;
import com.chaewsstore.exception.DuplicateException;
import com.chaewsstore.exception.NotFoundException;
import com.chaewsstore.repository.BidRepository;
import com.chaewsstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BidService {

    private final ProductRepository productRepository;
    private final BidRepository bidRepository;

    /**
     * 입찰을 생성한다.
     *
     * @param account   입찰을 생성하는 사용자의 계정
     * @param productId 입찰할 상품 ID
     * @param request   생성할 입찰에 대한 정보
     * @throws NotFoundException  상품이 존재하지 않는 경우
     * @throws DuplicateException 해당 상품에 이미 입찰한 경우
     */
    @Transactional
    public void createBid(Account account, Long productId, CreateBidRequestDto request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);
        if (bidRepository.existsByProductAndBidder(product, account)) {
            throw DUPLICATION_BID;
        }
        bidRepository.save(request.toEntity(product, account));
    }

}
