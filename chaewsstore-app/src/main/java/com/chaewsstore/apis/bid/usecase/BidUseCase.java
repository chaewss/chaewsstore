package com.chaewsstore.apis.bid.usecase;

import static com.chaewsstore.common.exception.ExceptionConstants.FORBIDDEN_BID;
import static com.chaewsstore.common.exception.ExceptionConstants.INSUFFICIENT_BALANCE;
import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_BID;
import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_BID_WITH_CONDITION;
import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_PRODUCT;
import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_USER;

import com.chaewsstore.apis.bid.dto.CreateBidRequestDto;
import com.chaewsstore.apis.bid.dto.ReadProductBidResponseDto;
import com.chaewsstore.apis.bid.dto.TransactBidRequestDto;
import com.chaewsstore.apis.bid.dto.UpdateBidRequestDto;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.bid.BidService;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.product.ProductService;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.user.UserService;
import com.globalutils.annotation.UseCase;
import com.globalutils.exception.BadRequestException;
import com.globalutils.exception.ForbiddenException;
import com.globalutils.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class BidUseCase {

    private final UserService userService;
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
        Product product = getProduct(productId);

        List<ReadProductBidResponseDto> response = bidService.readAllByProduct(product, pageable)
            .stream().map(ReadProductBidResponseDto::from).toList();
        return new SliceImpl<>(response);
    }

    /**
     * 구매 또는 판매 입찰을 생성한다.
     *
     * @param user      입찰을 생성하는 사용자의 계정
     * @param productId 입찰할 상품 ID
     * @param request   생성할 입찰에 대한 정보
     * @throws NotFoundException 상품이 존재하지 않는 경우
     */
    @Transactional
    public void createBid(User user, Long productId, CreateBidRequestDto request, BidType bidType) {
        Product product = getProduct(productId);

        Optional<Bid> optionalBid = bidService.readLiveBidByProductAndBidderAndType(product, user,
            bidType);
        if (optionalBid.isPresent()) {
            Bid liveBid = optionalBid.get();
            liveBid.updatePrice(request.price());
        } else {
            bidService.create(Bid.create(request.price(), product, user, bidType));
        }
    }

    /**
     * 판매 입찰을 처리하고 관련된 구매 입찰을 생성한다.
     *
     * @param user    구매 입찰을 생성하는 사용자
     * @param request 주문 요청 정보
     * @throws NotFoundException 주어진 조건의 판매 가능한 입찰이 없는 경우
     */
    @Transactional
    public void transactSellBid(User user, TransactBidRequestDto request) {
        Bid sellBid = getValidBid(request.productId(), request.price(), BidType.SELL);

        Bid buyBid = bidService.create(Bid.transactSellBidAndCreateBuyBid(user, sellBid));
        sellBid.relateBid(buyBid);
    }

    /**
     * 구매 입찰을 처리하고 관련된 판매 입찰을 생성한다.
     *
     * @param user    판매 입찰을 생성하는 사용자
     * @param request 주문 요청 정보
     * @throws NotFoundException 주어진 조건의 구매 가능한 입찰이 없는 경우
     */
    @Transactional
    public void transactBuyBid(User user, TransactBidRequestDto request) {
        Bid buyBid = getValidBid(request.productId(), request.price(), BidType.BUY);

        Bid sellBid = bidService.create(Bid.transactBuyBidAndCreateSellBid(user, buyBid));
        buyBid.relateBid(sellBid);
    }

    /**
     * 구매자가 입찰 상품 금액을 입금한다.
     *
     * @param buyer 입금을 수행하는 구매자
     * @param bidId 구매 입찰 ID
     * @throws NotFoundException   입찰이 존재하지 않는 경우
     * @throws BadRequestException 구매자의 계좌 잔액이 입금할 금액보다 적을 경우
     */
    @Transactional
    public void depositBid(User buyer, Long bidId) {
        Bid bid = getBid(bidId);

        Long price = bid.calculateFinalPrice(bid.getPrice());
        checkAccountBalance(buyer, price);

        performTransaction(buyer, bid, price);
        bid.updateStatusAfterDeposit();
    }

    /**
     * 입찰을 수정한다.
     *
     * @param user    현재 사용자의 계정
     * @param bidId   수정할 입찰 ID
     * @param request 수정할 입찰에 대한 정보
     * @throws NotFoundException  입찰이 존재하지 않는 경우
     * @throws ForbiddenException 현재 사용자가 해당 입찰의 입찰자가 아닌 경우
     */
    @Transactional
    public void updateBid(User user, Long bidId, UpdateBidRequestDto request) {
        Bid bid = getBid(bidId);
        if (!user.equals(bid.getBidder())) {
            throw FORBIDDEN_BID;
        }
        bid.updatePrice(request.price());
    }

    /**
     * 입찰을 삭제한다.
     *
     * @param user  현재 사용자의 계정
     * @param bidId 삭제할 입찰 ID
     * @throws NotFoundException  입찰이 존재하지 않는 경우
     * @throws ForbiddenException 현재 사용자가 해당 입찰의 입찰자가 아닌 경우
     */
    @Transactional
    public void deleteBid(User user, Long bidId) {
        Bid bid = getBid(bidId);
        if (!user.equals(bid.getBidder())) {
            throw FORBIDDEN_BID;
        }
        bidService.remove(bid);
    }

    private Product getProduct(Long productId) {
        return productService.readById(productId).orElseThrow(() -> NOT_FOUND_PRODUCT);
    }

    private Bid getBid(Long bidId) {
        return bidService.readById(bidId).orElseThrow(() -> NOT_FOUND_BID);
    }

    private Bid getValidBid(Long productId, Integer price, BidType bidType) {
        return bidService.readValidBid(productId, price, bidType)
            .orElseThrow(() -> NOT_FOUND_BID_WITH_CONDITION);
    }

    private void checkAccountBalance(User user, Long bidPrice) {
        if (user.getAccount() < bidPrice) {
            throw INSUFFICIENT_BALANCE;
        }
    }

    private void performTransaction(User buyer, Bid bid, Long price) {
        User lockBuyer = userService.readByIdWithOptimisticLock(buyer.getId())
            .orElseThrow(() -> NOT_FOUND_USER);
        lockBuyer.withdraw(price);

        Long sellerId = bid.getRelatedBid().getBidder().getId();
        User lockSeller = userService.readByIdWithOptimisticLock(sellerId)
            .orElseThrow(() -> NOT_FOUND_USER);
        lockSeller.deposit(price);
    }
}
