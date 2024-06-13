package com.chaewsstore.apis.bid.usecase;

import static com.chaewsstore.common.exception.ExceptionConstants.DUPLICATION_BID;
import static com.chaewsstore.common.exception.ExceptionConstants.FORBIDDEN_BID;
import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_BID;
import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_PRODUCT;

import com.chaewsstore.apis.bid.dto.CreateBidRequestDto;
import com.chaewsstore.apis.bid.dto.ReadProductBidResponseDto;
import com.chaewsstore.apis.bid.dto.UpdateBidRequestDto;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.bid.BidService;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.product.ProductService;
import com.globalutils.annotation.UseCase;
import com.globalutils.exception.DuplicateException;
import com.globalutils.exception.ForbiddenException;
import com.globalutils.exception.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class BidUseCase {

    private final ProductService productService;
    private final BidService bidService;

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
        Product product = productService.readById(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);

        List<ReadProductBidResponseDto> response = bidService.readAllByProduct(product, pageable)
            .stream().map(ReadProductBidResponseDto::from).toList();
        return new SliceImpl<>(response);
    }

    /**
     * 입찰을 생성한다.
     *
     * @param user   입찰을 생성하는 사용자의 계정
     * @param productId 입찰할 상품 ID
     * @param request   생성할 입찰에 대한 정보
     * @throws NotFoundException  상품이 존재하지 않는 경우
     * @throws DuplicateException 해당 상품에 이미 입찰한 경우
     */
    @Transactional
    public void createBid(User user, Long productId, CreateBidRequestDto request) {
        Product product = productService.readById(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);
        if (bidService.existsByProductAndBidder(product, user)) {
            throw DUPLICATION_BID;
        }
        bidService.create(request.toEntity(product, user));
    }

    /**
     * 입찰을 수정한다.
     *
     * @param user 현재 사용자의 계정
     * @param bidId   수정할 입찰 ID
     * @param request 수정할 입찰에 대한 정보
     * @throws NotFoundException  입찰이 존재하지 않는 경우
     * @throws ForbiddenException 현재 사용자가 해당 입찰의 입찰자가 아닌 경우
     */
    @Transactional
    public void updateBid(User user, Long bidId, UpdateBidRequestDto request) {
        Bid bid = bidService.readById(bidId).orElseThrow(() -> NOT_FOUND_BID);
        if (!user.equals(bid.getBidder())) {
            throw FORBIDDEN_BID;
        }
        bid.updatePrice(request.price());
    }

    /**
     * 입찰을 삭제한다.
     *
     * @param user 현재 사용자의 계정
     * @param bidId   삭제할 입찰 ID
     * @throws NotFoundException   입찰이 존재하지 않는 경우
     * @throws ForbiddenException  현재 사용자가 해당 입찰의 입찰자가 아닌 경우
     */
    @Transactional
    public void deleteBid(User user, Long bidId) {
        Bid bid = bidService.readById(bidId).orElseThrow(() -> NOT_FOUND_BID);
        if (!user.equals(bid.getBidder())) {
            throw FORBIDDEN_BID;
        }
        bidService.remove(bid);
    }
}
