package com.chaewsstore.service;

import static com.chaewsstore.exception.ExceptionConstants.DUPLICATION_BID;
import static com.chaewsstore.exception.ExceptionConstants.NOT_FOUND_PRODUCT;

import com.chaewsstore.dto.bid.CreateBidRequestDto;
import com.chaewsstore.dto.bid.ReadProductBidResponseDto;
import com.chaewsstore.entity.Account;
import com.chaewsstore.entity.Product;
import com.chaewsstore.exception.DuplicateException;
import com.chaewsstore.exception.NotFoundException;
import com.chaewsstore.repository.BidRepository;
import com.chaewsstore.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BidService {

    private final ProductRepository productRepository;
    private final BidRepository bidRepository;

    /**
     * 상품의 입찰 목록을 조회한다.
     *
     * @param productId 상품 ID
     * @param pageable  페이지 정보
     * @return 상품의 입찰 목록
     * @throws NotFoundException 상품이 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Slice<ReadProductBidResponseDto> readProductBidList(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);

        List<ReadProductBidResponseDto> response = bidRepository.findAllByProduct(product, pageable)
            .stream().map(ReadProductBidResponseDto::from).toList();
        return new SliceImpl<>(response);
    }

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
