package com.chaewsstore.app.apis.bid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.chaewsstore.apis.bid.dto.CreateBidRequestDto;
import com.chaewsstore.apis.bid.dto.ReadProductBidResponseDto;
import com.chaewsstore.apis.bid.dto.TransactBidRequestDto;
import com.chaewsstore.apis.bid.dto.UpdateBidRequestDto;
import com.chaewsstore.apis.bid.usecase.BidUseCase;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.bid.BidErrorCode;
import com.chaewsstore.core.domain.bid.BidService;
import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.product.ProductErrorCode;
import com.chaewsstore.core.domain.product.ProductService;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.user.UserErrorCode;
import com.chaewsstore.core.domain.user.UserService;
import com.globalutils.exception.BadRequestException;
import com.globalutils.exception.ForbiddenException;
import com.globalutils.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@ExtendWith(MockitoExtension.class)
class BidUseCaseTest {

    @InjectMocks
    private BidUseCase bidUseCase;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private BidService bidService;

    @Test
    @DisplayName("상품에 대한 입찰 목록을 조회한다")
    void succeed_to_read_product_bid_list() {
        given(productService.readById(anyLong())).willReturn(Optional.of(product));
        given(bidService.readAllByProduct(product, Pageable.unpaged())).willReturn(
            productBidQueryDtoList);

        Slice<ReadProductBidResponseDto> result = bidUseCase.readProductBidList(anyLong(),
            Pageable.unpaged());

        assertNotNull(result);
        assertEquals(productBidQueryDtoList.size(), result.getContent().size());
        assertFalse(result.hasNext());
        then(productService).should(times(1)).readById(anyLong());
        then(bidService).should(times(1)).readAllByProduct(any(), any());
    }

    @Test
    @DisplayName("상품이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_read_product_bid_list_but_product_does_not_exist() {
        given(productService.readById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
            () -> bidUseCase.readProductBidList(999L, Pageable.unpaged()));

        then(productService).should(times(1)).readById(anyLong());
    }

    @ParameterizedTest
    @EnumSource(mode = Mode.INCLUDE, names = {"SELL", "BUY"})
    @DisplayName("판매 입찰과 구매 입찰을 정상적으로 추가한다")
    void succeed_to_create_bid(BidType bidType) {
        CreateBidRequestDto request = new CreateBidRequestDto(6000);

        given(productService.readById(anyLong())).willReturn(Optional.of(product));
        given(bidService.readLiveBidByProductAndBidderAndType(any(), any(), any())).willReturn(
            Optional.empty());
        given(bidService.create(any())).willReturn(any());

        bidUseCase.createBid(user, 1L, request, bidType);

        then(productService).should(times(1)).readById(anyLong());
        then(bidService).should(times(1)).readLiveBidByProductAndBidderAndType(any(), any(), any());
        then(bidService).should(times(1)).create(any());
    }

    @Test
    @DisplayName("입찰 생성 시 기존 입찰이 이미 있을 경우 정상적으로 수정한다")
    void succeed_to_create_sell_bid_but_update() {
        CreateBidRequestDto request = new CreateBidRequestDto(6000);

        given(productService.readById(anyLong())).willReturn(Optional.of(product));
        given(bidService.readLiveBidByProductAndBidderAndType(any(), any(), any())).willReturn(
            Optional.of(buyBidLive));

        bidUseCase.createBid(user, 1L, request, BidType.BUY);

        then(productService).should(times(1)).readById(anyLong());
        then(bidService).should(times(1)).readLiveBidByProductAndBidderAndType(any(), any(), any());
    }

    @Test
    @DisplayName("입찰 생성 시 상품이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_create_bid_but_product_does_not_exist() {
        CreateBidRequestDto request = new CreateBidRequestDto(6000);

        given(productService.readById(anyLong())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> bidUseCase.createBid(user, 999L, request, BidType.SELL));

        then(productService).should(times(1)).readById(anyLong());
        assertEquals(ProductErrorCode.NOT_FOUND_PRODUCT, result.getResponseCode());
    }

    @Test
    @DisplayName("판매 입찰을 처리하고 관련된 구매 입찰을 정상적으로 추가한다")
    void succeed_to_transact_sell_bid_and_create_buy_bid() {
        TransactBidRequestDto request = new TransactBidRequestDto(1L, 6000);

        given(bidService.readValidBid(anyLong(), any(), any())).willReturn(Optional.of(sellBidLive));
        given(bidService.create(any())).willReturn(buyBidLive);

        bidUseCase.transactSellBid(user, request);

        then(bidService).should(times(1)).readValidBid(anyLong(), any(), any());
        then(bidService).should(times(1)).create(any());
    }

    @Test
    @DisplayName("주어진 조건의 판매 가능한 입찰이 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_transact_sell_bid_but_bid_does_not_exist() {
        TransactBidRequestDto request = new TransactBidRequestDto(999L, 6000);

        given(bidService.readValidBid(anyLong(), any(), any())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> bidUseCase.transactSellBid(user, request));

        then(bidService).should(times(1)).readValidBid(anyLong(), any(), any());
        assertEquals(BidErrorCode.NOT_FOUND_BID_WITH_CONDITION, result.getResponseCode());
    }

    @Test
    @DisplayName("구매 입찰을 처리하고 관련된 판매 입찰을 정상적으로 추가한다")
    void succeed_to_transact_buy_bid_and_create_sell_bid() {
        TransactBidRequestDto request = new TransactBidRequestDto(1L, 6000);

        given(bidService.readValidBid(anyLong(), any(), any())).willReturn(Optional.of(buyBidLive));
        given(bidService.create(any())).willReturn(sellBidLive);

        bidUseCase.transactSellBid(user, request);

        then(bidService).should(times(1)).readValidBid(anyLong(), any(), any());
        then(bidService).should(times(1)).create(any());
    }

    @Test
    @DisplayName("주어진 조건의 구매 가능한 입찰이 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_transact_buy_bid_but_bid_does_not_exist() {
        TransactBidRequestDto request = new TransactBidRequestDto(999L, 6000);

        given(bidService.readValidBid(anyLong(), any(), any())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> bidUseCase.transactBuyBid(user, request));

        then(bidService).should(times(1)).readValidBid(anyLong(), any(), any());
        assertEquals(BidErrorCode.NOT_FOUND_BID_WITH_CONDITION, result.getResponseCode());
    }

    @Test
    @DisplayName("구매자가 입찰 상품 금액을 정상적으로 입금한다")
    void succeed_to_deposit_bid_when_authenticated() {
        buyBidInTransaction.relateBid(sellBidAuthenticated);

        Long sellerBeforeBalance = anotherUser.getAccount();
        Long buyerBeforeBalance = user.getAccount();

        given(bidService.readById(anyLong())).willReturn(Optional.of(buyBidInTransaction));
        given(userService.readByIdWithOptimisticLock(anyLong())).willReturn(Optional.of(user))
            .willReturn(Optional.of(anotherUser));

        bidUseCase.depositBid(user, anyLong());

        Integer price = buyBidInTransaction.getPrice();
        assertEquals(sellerBeforeBalance + price, anotherUser.getAccount());
        assertEquals(buyerBeforeBalance - price, user.getAccount());
        assertEquals(Status.DELIVERING, buyBidInTransaction.getStatus());
        assertEquals(Status.FINISHED, buyBidInTransaction.getRelatedBid().getStatus());
        then(bidService).should(times(1)).readById(anyLong());
        then(userService).should(times(2)).readByIdWithOptimisticLock(anyLong());
    }

    @Test
    @DisplayName("구매자가 입찰 상품 금액 * 0.85를 정상적으로 입금한다")
    void succeed_to_deposit_bid_when_accredited() {
        Bid sellBidAccredited = Bid.builder()
            .bidder(anotherUser)
            .price(6000)
            .bidType(BidType.SELL)
            .status(Status.ACCREDITED)
            .build();
        Bid buyBidInTransactionAcc = Bid.builder()
            .bidder(user)
            .price(6000)
            .bidType(BidType.BUY)
            .status(Status.IN_TRANSACTION)
            .relatedBid(sellBidAccredited)
            .build();

        Long sellerBeforeBalance = anotherUser.getAccount();
        Long buyerBeforeBalance = user.getAccount();

        given(bidService.readById(anyLong())).willReturn(Optional.of(buyBidInTransactionAcc));
        given(userService.readByIdWithOptimisticLock(anyLong())).willReturn(Optional.of(user))
            .willReturn(Optional.of(anotherUser));

        bidUseCase.depositBid(user, anyLong());

        Long price = Math.round(buyBidInTransactionAcc.getPrice() * 0.85);
        assertEquals(sellerBeforeBalance + price, anotherUser.getAccount());
        assertEquals(buyerBeforeBalance - price, user.getAccount());
        assertEquals(Status.DELIVERING, buyBidInTransactionAcc.getStatus());
        assertEquals(Status.FINISHED, buyBidInTransactionAcc.getRelatedBid().getStatus());
        then(bidService).should(times(1)).readById(anyLong());
        then(userService).should(times(2)).readByIdWithOptimisticLock(anyLong());
    }

    @Test
    @DisplayName("구매자 입금 시 해당 구매 입찰이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_deposit_bid_but_bid_does_not_exist() {
        given(bidService.readById(anyLong())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> bidUseCase.depositBid(user, 999L));

        then(bidService).should(times(1)).readById(anyLong());
        assertEquals(BidErrorCode.NOT_FOUND_BID, result.getResponseCode());
    }

    @Test
    @DisplayName("구매자 입금 시 아직 상품이 검수되지 않은 경우 BadRequestException이 발생한다")
    void should_throw_BadRequestException_when_deposit_bid_but_bid_not_inspect() {
        Bid notInspectSellBid = Bid.builder()
            .bidder(anotherUser)
            .price(6000)
            .bidType(BidType.SELL)
            .status(Status.IN_TRANSACTION)
            .build();
        Bid buyBid = Bid.builder()
            .bidder(user)
            .price(6000)
            .bidType(BidType.BUY)
            .status(Status.IN_TRANSACTION)
            .relatedBid(notInspectSellBid)
            .build();

        given(bidService.readById(anyLong())).willReturn(Optional.of(buyBid));

        BadRequestException result = assertThrows(BadRequestException.class,
            () -> bidUseCase.depositBid(user, anyLong()));

        then(bidService).should(times(1)).readById(anyLong());
        assertEquals(BidErrorCode.BID_NOT_INSPECT, result.getResponseCode());
    }

    @Test
    @DisplayName("구매자 입금 시 구매자 계좌 잔고가 부족할 경우 BadRequestException이 발생한다")
    void should_throw_BadRequestException_when_deposit_bid_but_insufficient_balance() {
        Bid expensiveSellBid = Bid.builder()
            .bidder(anotherUser)
            .price(1000000000)
            .bidType(BidType.SELL)
            .status(Status.AUTHENTICATED)
            .build();
        Bid expensiveBuyBid = Bid.builder()
            .bidder(user)
            .price(1000000000)
            .bidType(BidType.BUY)
            .status(Status.IN_TRANSACTION)
            .relatedBid(expensiveSellBid)
            .build();

        given(bidService.readById(anyLong())).willReturn(Optional.of(expensiveBuyBid));

        BadRequestException result = assertThrows(BadRequestException.class,
            () -> bidUseCase.depositBid(user, anyLong()));

        then(bidService).should(times(1)).readById(anyLong());
        assertEquals(UserErrorCode.INSUFFICIENT_BALANCE, result.getResponseCode());
    }

    @Test
    @DisplayName("입찰을 정상적으로 수정한다")
    void succeed_to_update_bid() {
        UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

        given(bidService.readById(anyLong())).willReturn(Optional.of(buyBidLive));

        bidUseCase.updateBid(user, 1L, request);

        assertEquals(request.price(), buyBidLive.getPrice());
        then(bidService).should(times(1)).readById(anyLong());
    }

    @Test
    @DisplayName("입찰이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFountException_when_update_bid_but_bid_does_not_exist() {
        UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

        given(bidService.readById(anyLong())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> bidUseCase.updateBid(user, 999L, request));

        then(bidService).should(times(1)).readById(anyLong());
        assertEquals(BidErrorCode.NOT_FOUND_BID, result.getResponseCode());
    }

    @Test
    @DisplayName("입찰자가 아닌 사용자가 입찰 수정을 시도할 경우 ForbiddenException이 발생한다")
    void should_throw_ForbiddenException_when_update_bid_but_user_is_not_bidder() {
        UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

        given(bidService.readById(anyLong())).willReturn(Optional.of(buyBidLive));

        ForbiddenException result = assertThrows(ForbiddenException.class,
            () -> bidUseCase.updateBid(anotherUser, 1L, request));

        then(bidService).should(times(1)).readById(anyLong());
        assertEquals(BidErrorCode.FORBIDDEN_BID, result.getResponseCode());
    }

    @Test
    @DisplayName("입찰이 이미 진행중인데 입찰 수정을 시도할 경우 BadRequestException이 발생한다")
    void should_throw_BadRequestException_when_update_bid_but_bid_status_is_not_live() {
        UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

        given(bidService.readById(anyLong())).willReturn(Optional.of(buyBidInTransaction));

        BadRequestException result = assertThrows(BadRequestException.class,
            () -> bidUseCase.updateBid(user, anyLong(), request));

        then(bidService).should(times(1)).readById(anyLong());
        assertEquals(BidErrorCode.BID_NOT_IN_LIVE, result.getResponseCode());
    }

    @Test
    @DisplayName("특정 입찰을 정상적으로 삭제한다")
    void succeed_to_delete_bid() {
        given(bidService.readById(any())).willReturn(Optional.of(buyBidLive));

        bidUseCase.deleteBid(user, 1L);

        then(bidService).should(times(1)).readById(anyLong());
        then(bidService).should(times(1)).remove(any());
    }

    @Test
    @DisplayName("입찰이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFountException_when_delete_bid_but_bid_does_not_exist() {
        given(bidService.readById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bidUseCase.deleteBid(user, 1L));

        then(bidService).should(times(1)).readById(anyLong());
    }

    @Test
    @DisplayName("입찰자가 아닌 사용자가 입찰 삭제를 시도할 경우 ForbiddenException이 발생한다")
    void should_throw_ForbiddenException_when_delete_bid_but_user_is_not_bidder() {
        given(bidService.readById(anyLong())).willReturn(Optional.of(buyBidLive));

        assertThrows(ForbiddenException.class, () -> bidUseCase.deleteBid(anotherUser, 1L));

        then(bidService).should(times(1)).readById(anyLong());
    }

    User user = User.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("aaaa1111!!")
        .nickname("닉네임")
        .account(10000L)
        .build();
    User anotherUser = User.builder()
        .id(2L)
        .username("another@gmail.com")
        .password("aaaa1111!!")
        .nickname("닉네임2")
        .account(0L)
        .build();

    Product product = Product.builder().build();
    Bid buyBidLive = Bid.builder()
        .bidder(user)
        .price(30)
        .bidType(BidType.BUY)
        .status(Status.LIVE)
        .build();
    Bid sellBidLive = Bid.builder()
        .bidder(anotherUser)
        .price(30)
        .bidType(BidType.SELL)
        .status(Status.LIVE)
        .build();
    Bid sellBidAuthenticated = Bid.builder()
        .bidder(anotherUser)
        .price(6000)
        .bidType(BidType.SELL)
        .status(Status.AUTHENTICATED)
        .build();
    Bid buyBidInTransaction = Bid.builder()
        .bidder(user)
        .price(6000)
        .bidType(BidType.BUY)
        .status(Status.IN_TRANSACTION)
        .build();

    List<ReadProductBidQueryDto> productBidQueryDtoList = List.of(
        new ReadProductBidQueryDto(7000, 1L), new ReadProductBidQueryDto(8000, 3L),
        new ReadProductBidQueryDto(1000, 1L));
}
