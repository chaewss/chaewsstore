package com.chaewsstore.app.apis.bid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.chaewsstore.app.apis.bid.dto.CreateBidRequestDto;
import com.chaewsstore.app.apis.bid.dto.ReadProductBidResponseDto;
import com.chaewsstore.app.apis.bid.dto.TransactBidRequestDto;
import com.chaewsstore.app.apis.bid.dto.UpdateBidRequestDto;
import com.chaewsstore.app.apis.bid.usecase.BidUseCase;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@DisplayName("BidUseCase 클래스")
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

    @Nested
    @DisplayName("readProductBidList 메서드는")
    class read_product_bid_list {

        @ParameterizedTest(name = "@param bidType: {0}")
        @NullSource
        @EnumSource(value = BidType.class)
        @DisplayName("상품에 대한 입찰 목록 조회에 성공하면 페이지네이션 된 결과를 반환한다")
        void succeed_to_read_product_bid_list(BidType bidType) {
            given(productService.readById(anyLong())).willReturn(Optional.of(product));
            if (bidType != null) {
                given(bidService.readAllByProduct(product, bidType, Pageable.unpaged()))
                    .willReturn(productBidQueryDtoListWithParam);
            } else {
                given(bidService.readAllByProduct(product, null, Pageable.unpaged()))
                    .willReturn(productBidQueryDtoListWithoutParam);
            }

            Slice<ReadProductBidResponseDto> result = bidUseCase.readProductBidList(anyLong(),
                bidType, Pageable.unpaged());

            assertNotNull(result);
            if (bidType != null) {
                assertEquals(productBidQueryDtoListWithParam.size(), result.getContent().size());
            } else {
                assertEquals(productBidQueryDtoListWithoutParam.size(), result.getContent().size());
            }
            assertFalse(result.hasNext());
            then(productService).should(times(1)).readById(anyLong());
            then(bidService).should(times(1)).readAllByProduct(any(), any(), any());
        }

        @Test
        @DisplayName("상품이 존재하지 않는 경우 NotFoundException이 발생한다")
        void should_throw_NotFoundException_when_read_product_bid_list_but_product_does_not_exist() {
            given(productService.readById(anyLong())).willReturn(Optional.empty());

            NotFoundException result = assertThrows(NotFoundException.class,
                () -> bidUseCase.readProductBidList(999L, any(), Pageable.unpaged()));

            then(productService).should(times(1)).readById(anyLong());
            assertEquals(ProductErrorCode.NOT_FOUND_PRODUCT, result.getResponseCode());
        }
    }

    @Nested
    @DisplayName("createBid 메서드는")
    class create_bid {

        @ParameterizedTest(name = "{0} 입찰")
        @EnumSource(mode = Mode.INCLUDE, names = {"SELL", "BUY"})
        @DisplayName("입찰 정보로 새로운 구매 또는 판매 입찰을 생성한다")
        void succeed_to_create_bid(BidType bidType) {
            CreateBidRequestDto request = new CreateBidRequestDto(6000);

            given(productService.readById(anyLong())).willReturn(Optional.of(product));
            given(bidService.readLiveBidByProductAndBidderAndType(any(), any(), any())).willReturn(
                Optional.empty());
            given(bidService.create(any())).willReturn(any());

            bidUseCase.createBid(user, 1L, request, bidType);

            then(productService).should(times(1)).readById(anyLong());
            then(bidService).should(times(1))
                .readLiveBidByProductAndBidderAndType(any(), any(), any());
            then(bidService).should(times(1)).create(any());
        }

        @ParameterizedTest(name = "{0} 입찰")
        @EnumSource(mode = Mode.INCLUDE, names = {"SELL", "BUY"})
        @DisplayName("같은 사용자 계정으로 해당 상품에 대한 입찰이 이미 있을 경우 가격을 수정한다")
        void succeed_to_create_sell_bid_but_update(BidType bidType) {
            CreateBidRequestDto request = new CreateBidRequestDto(6000);

            given(productService.readById(anyLong())).willReturn(Optional.of(product));
            given(bidService.readLiveBidByProductAndBidderAndType(any(), any(), any())).willReturn(
                Optional.of(buyBidLive));

            bidUseCase.createBid(user, 1L, request, bidType);

            then(productService).should(times(1)).readById(anyLong());
            then(bidService).should(times(1))
                .readLiveBidByProductAndBidderAndType(any(), any(), any());
        }

        @Test
        @DisplayName("상품이 존재하지 않는 경우 NotFoundException이 발생한다")
        void should_throw_NotFoundException_when_create_bid_but_product_does_not_exist() {
            CreateBidRequestDto request = new CreateBidRequestDto(6000);

            given(productService.readById(anyLong())).willReturn(Optional.empty());

            NotFoundException result = assertThrows(NotFoundException.class,
                () -> bidUseCase.createBid(user, 999L, request, BidType.SELL));

            then(productService).should(times(1)).readById(anyLong());
            assertEquals(ProductErrorCode.NOT_FOUND_PRODUCT, result.getResponseCode());
        }
    }

    @Nested
    @DisplayName("transactSellBid 메서드는")
    class transact_sell_bid {

        @Test
        @DisplayName("LIVE 상태인 판매 입찰을 IN_TRANSACTION으로 변경하고 관련된 구매 입찰을 생성한다")
        void succeed_to_transact_sell_bid_and_create_buy_bid() {
            TransactBidRequestDto request = new TransactBidRequestDto(1L, 6000);

            given(bidService.readValidBid(anyLong(), any(), any())).willReturn(
                Optional.of(sellBidLive));
            given(bidService.create(any())).willReturn(buyBidInTransaction);

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
    }

    @Nested
    @DisplayName("transactBuyBid 메서드는")
    class transact_buy_bid {

        @Test
        @DisplayName("LIVE 상태인 구매 입찰을 IN_TRANSACTION으로 변경하고 관련된 판매 입찰을 생성한다")
        void succeed_to_transact_buy_bid_and_create_sell_bid() {
            TransactBidRequestDto request = new TransactBidRequestDto(1L, 6000);

            given(bidService.readValidBid(anyLong(), any(), any())).willReturn(
                Optional.of(buyBidLive));
            given(bidService.create(any())).willReturn(sellBidInTransaction);

            bidUseCase.transactBuyBid(user, request);

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
    }

    @Nested
    @DisplayName("depositBid 메서드는")
    class deposit_bid {

        @Test
        @DisplayName("구매 입찰이 IN_TRANSACTION 상태이고, 관련 판매 입찰이 AUTHENTICATED 상태인 경우 구매자가 입찰 금액을 판매자에게 입금한다")
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
        @DisplayName("구매 입찰이 IN_TRANSACTION 상태이고, 관련 판매 입찰이 ACCREDITED 상태인 경우 구매자가 입찰 금액 * 0.85을 판매자에게 입금한다")
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
        @DisplayName("해당 구매 입찰이 존재하지 않는 경우 NotFoundException이 발생한다")
        void should_throw_NotFoundException_when_deposit_bid_but_bid_does_not_exist() {
            given(bidService.readById(anyLong())).willReturn(Optional.empty());

            NotFoundException result = assertThrows(NotFoundException.class,
                () -> bidUseCase.depositBid(user, 999L));

            then(bidService).should(times(1)).readById(anyLong());
            assertEquals(BidErrorCode.NOT_FOUND_BID, result.getResponseCode());
        }

        @Test
        @DisplayName("아직 상품이 검수되지 않은 경우 BadRequestException이 발생한다")
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
        @DisplayName("구매자 계좌 잔고가 부족할 경우 BadRequestException이 발생한다")
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
    }

    @Nested
    @DisplayName("updateBid 메서드는")
    class update_bid {

        @Test
        @DisplayName("입찰 아이디와 정보로 입찰을 수정한다")
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
        @DisplayName("입찰이 이미 진행중인 경우 BadRequestException이 발생한다")
        void should_throw_BadRequestException_when_update_bid_but_bid_status_is_not_live() {
            UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

            given(bidService.readById(anyLong())).willReturn(Optional.of(buyBidInTransaction));

            BadRequestException result = assertThrows(BadRequestException.class,
                () -> bidUseCase.updateBid(user, anyLong(), request));

            then(bidService).should(times(1)).readById(anyLong());
            assertEquals(BidErrorCode.BID_NOT_IN_LIVE, result.getResponseCode());
        }
    }

    @Nested
    @DisplayName("deleteBid 메서드는")
    class delete_bid {

        @ParameterizedTest(name = "status가 {1}인 경우 {3}를 반환한다.")
        @MethodSource("cancellableBid")
        @DisplayName("입찰 아이디로 특정 입찰을 삭제한다")
        void succeed_to_delete_bid(User user, Status status, Bid bid, boolean shouldFlush) {
            given(bidService.readById(anyLong())).willReturn(Optional.of(bid));
            doNothing().when(bidService).remove(any(Bid.class));

            bidUseCase.deleteBid(user, anyLong());

            then(bidService).should(times(1)).readById(anyLong());
            if (shouldFlush) {
                then(bidService).should(times(1)).flush();
            } else {
                then(bidService).should(never()).flush();
            }
            then(bidService).should(times(1)).remove(any());
        }

        static Stream<Arguments> cancellableBid() {
            User user = User.builder().build();

            Bid cancelledBid = Bid.builder()
                .bidder(user)
                .status(Status.CANCELLED)
                .build();

            Bid authenticatedFailedBid = Bid.builder()
                .bidder(user)
                .status(Status.AUTHENTICATED_FAILED)
                .build();

            Bid finishedBid = Bid.builder()
                .bidder(user)
                .status(Status.FINISHED)
                .build();

            Bid expiredBid = Bid.builder()
                .bidder(user)
                .status(Status.EXPIRED)
                .build();

            Bid liveBid = Bid.builder()
                .bidder(user)
                .status(Status.LIVE)
                .build();

            Bid inTransactionBuyBidI = Bid.builder()
                .bidder(user)
                .status(Status.IN_TRANSACTION)
                .bidType(BidType.BUY)
                .build();
            Bid inTransactionSellBid = Bid.builder()
                .status(Status.IN_TRANSACTION)
                .bidType(BidType.SELL)
                .build();
            inTransactionBuyBidI.relateBid(inTransactionSellBid);

            return Stream.of(
                arguments(user, Status.CANCELLED, cancelledBid, false),
                arguments(user, Status.AUTHENTICATED_FAILED, authenticatedFailedBid, false),
                arguments(user, Status.FINISHED, finishedBid, false),
                arguments(user, Status.EXPIRED, expiredBid, false),
                arguments(user, Status.LIVE, liveBid, true),
                arguments(user, Status.IN_TRANSACTION, inTransactionBuyBidI, true)
            );
        }

        @Test
        @DisplayName("입찰이 존재하지 않는 경우 NotFoundException이 발생한다")
        void should_throw_NotFountException_when_delete_bid_but_bid_does_not_exist() {
            given(bidService.readById(anyLong())).willReturn(Optional.empty());

            NotFoundException result = assertThrows(NotFoundException.class,
                () -> bidUseCase.deleteBid(user, 1L));

            then(bidService).should(times(1)).readById(anyLong());
            assertEquals(BidErrorCode.NOT_FOUND_BID, result.getResponseCode());
        }

        @Test
        @DisplayName("입찰자가 아닌 사용자가 입찰 삭제를 시도할 경우 ForbiddenException이 발생한다")
        void should_throw_ForbiddenException_when_delete_bid_but_user_is_not_bidder() {
            given(bidService.readById(anyLong())).willReturn(Optional.of(buyBidLive));

            ForbiddenException result = assertThrows(ForbiddenException.class,
                () -> bidUseCase.deleteBid(anotherUser, 1L));

            then(bidService).should(times(1)).readById(anyLong());
            assertEquals(BidErrorCode.FORBIDDEN_BID, result.getResponseCode());
        }

        @ParameterizedTest(name = "status가 {1}인 경우")
        @MethodSource("uncancellableBid")
        @DisplayName("입찰을 취소할 수 없는 경우 BadRequestException 발생한다")
        void should_throw_BadRequestException_when_delete_bid_but_bid_can_not_delete(User user,
            Status status, Bid bid) {
            given(bidService.readById(anyLong())).willReturn(Optional.of(bid));

            BadRequestException result = assertThrows(BadRequestException.class,
                () -> bidUseCase.deleteBid(user, anyLong()));

            then(bidService).should(times(1)).readById(anyLong());
            assertEquals(BidErrorCode.BID_CANNOT_CANCEL, result.getResponseCode());
        }

        static Stream<Arguments> uncancellableBid() {
            User user = User.builder().build();

            Bid authenticatedBid = Bid.builder()
                .bidder(user)
                .status(Status.AUTHENTICATED)
                .build();

            Bid accreditedBid = Bid.builder()
                .bidder(user)
                .status(Status.ACCREDITED)
                .build();

            Bid deliveringBid = Bid.builder()
                .bidder(user)
                .status(Status.DELIVERING)
                .build();

            Bid deliveredBid = Bid.builder()
                .bidder(user)
                .status(Status.DELIVERED)
                .build();

            return Stream.of(
                arguments(user, Status.AUTHENTICATED, authenticatedBid),
                arguments(user, Status.ACCREDITED, accreditedBid),
                arguments(user, Status.DELIVERING, deliveringBid),
                arguments(user, Status.DELIVERED, deliveredBid)
            );
        }
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
    Bid sellBidInTransaction = Bid.builder()
        .bidder(anotherUser)
        .price(6000)
        .bidType(BidType.SELL)
        .status(Status.IN_TRANSACTION)
        .build();

    List<ReadProductBidQueryDto> productBidQueryDtoListWithParam = List.of(
        new ReadProductBidQueryDto(7000, 1L), new ReadProductBidQueryDto(8000, 3L),
        new ReadProductBidQueryDto(1000, 1L));
    List<ReadProductBidQueryDto> productBidQueryDtoListWithoutParam = List.of(
        new ReadProductBidQueryDto(60000, LocalDateTime.now()),
        new ReadProductBidQueryDto(78000, LocalDateTime.now().minusDays(3)));
}
