package com.chaewsstore.service;

import static com.chaewsstore.exception.ExceptionConstants.DUPLICATION_BID;
import static com.chaewsstore.exception.ExceptionConstants.FORBIDDEN_BID;
import static com.chaewsstore.exception.ExceptionConstants.NOT_FOUND_BID;
import static com.chaewsstore.exception.ExceptionConstants.NOT_FOUND_PRODUCT;

import com.chaewsstore.dto.bid.CreateBidRequestDto;
import com.chaewsstore.dto.bid.ReadProductBidResponseDto;
import com.chaewsstore.dto.bid.UpdateBidRequestDto;
import com.chaewsstore.entity.Account;
import com.chaewsstore.entity.Bid;
import com.chaewsstore.entity.Product;
import com.chaewsstore.exception.DuplicateException;
import com.chaewsstore.exception.ForbiddenException;
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

    /**
     * 입찰을 수정한다.
     *
     * @param account 현재 사용자의 계정
     * @param bidId   수정할 입찰 ID
     * @param request 수정할 입찰에 대한 정보
     * @throws NotFoundException  입찰이 존재하지 않는 경우
     * @throws ForbiddenException 현재 사용자가 해당 입찰의 입찰자가 아닌 경우
     */
    @Transactional
    public void updateBid(Account account, Long bidId, UpdateBidRequestDto request) {
        Bid bid = bidRepository.findById(bidId).orElseThrow(() -> NOT_FOUND_BID);
        if (!account.equals(bid.getBidder())) {
            throw FORBIDDEN_BID;
        }
        bid.updatePrice(request.price());
    }

    /**
     * 입찰을 삭제한다.
     *
     * @param account 현재 사용자의 계정
     * @param bidId   삭제할 입찰 ID
     * @throws NotFoundException   입찰이 존재하지 않는 경우
     * @throws ForbiddenException  현재 사용자가 해당 입찰의 입찰자가 아닌 경우
     */
    @Transactional
    public void deleteBid(Account account, Long bidId) {
        Bid bid = bidRepository.findById(bidId).orElseThrow(() -> NOT_FOUND_BID);
        if (!account.equals(bid.getBidder())) {
            throw FORBIDDEN_BID;
        }
        bidRepository.delete(bid);
    }
}
