package com.chaewsstore.core.domain.bid;

import static com.chaewsstore.core.domain.BidFixture.SELL_BID_AUTHENTICATED;
import static com.chaewsstore.core.domain.BidFixture.SELL_BID_IN_TRANSACTION;
import static com.chaewsstore.core.domain.ProductFixture.PRODUCT;
import static com.chaewsstore.core.domain.UserFixture.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.user.User;
import com.globalutils.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

@DisplayName("Bid 클래스")
class BidTest {

    @BeforeEach
    void setUp() {
        user = USER.getUser();
        product = PRODUCT.getProduct();
        bid = Bid.create(700, product, user, Bid.BidType.BUY);
    }

    @Test
    @DisplayName("create 메서드는 입찰 생성 시, 초기 값들을 올바르게 설정한다")
    void should_create_bid() {
        assertThat(bid.getPrice()).isEqualTo(700);
        assertThat(bid.getProduct()).isEqualTo(product);
        assertThat(bid.getBidder()).isEqualTo(user);
        assertThat(bid.getStatus()).isEqualTo(Status.LIVE);
        assertThat(bid.getBidType()).isEqualTo(BidType.BUY);
        assertThat(bid.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("updatePrice 메서드는 입찰 가격을 업데이트하면 가격이 변경된다")
    void should_update_bid_price() {
        // when
        bid.updatePrice(2000);

        // then
        assertThat(bid.getPrice()).isEqualTo(2000);
    }

    @Nested
    @DisplayName("transactBid 메서드 중")
    class transactBid {

        @Test
        @DisplayName("transactSellBidAndCreateBuyBid 메서드는 판매 입찰을 거래 처리하고 구매 입찰을 생성한다")
        void should_transact_sell_bid_and_create_buy_bid() {
            // given
            Bid sellBid = Bid.create(1500, product, user, Bid.BidType.SELL);

            // when
            Bid buyBid = Bid.transactSellBidAndCreateBuyBid(user, sellBid);

            // then
            assertThat(sellBid.getStatus()).isEqualTo(Status.IN_TRANSACTION);
            assertThat(buyBid.getStatus()).isEqualTo(Status.IN_TRANSACTION);
            assertThat(buyBid.getRelatedBid()).isEqualTo(sellBid);
        }

        @Test
        @DisplayName("transactBuyBidAndCreateSellBid 메서드는 구매 입찰을 거래 처리하고 판매 입찰을 생성한다")
        void should_transact_buy_bid_and_create_sell_bid() {
            // given
            Bid buyBid = Bid.create(1500, product, user, Bid.BidType.BUY);

            // when
            Bid sellBid = Bid.transactSellBidAndCreateBuyBid(user, buyBid);

            // then
            assertThat(buyBid.getStatus()).isEqualTo(Status.IN_TRANSACTION);
            assertThat(sellBid.getStatus()).isEqualTo(Status.IN_TRANSACTION);
            assertThat(sellBid.getRelatedBid()).isEqualTo(buyBid);
        }
    }

    @Test
    @DisplayName("relateBid 메서드는 관련 입찰을 올바르게 설정한다")
    void should_relate_bid() {
        // given
        Bid relatedBid = SELL_BID_IN_TRANSACTION.getBid();

        // when
        bid.relateBid(relatedBid);

        // then
        assertThat(bid.getRelatedBid()).isEqualTo(relatedBid);
    }

    @Nested
    @DisplayName("inspect 메서드는")
    class inspect {

        @Test
        @DisplayName("IN_TRANSACTION 상태에서 100점을 받으면 상태가 AUTHENTICATED로 변경된다")
        void should_update_status_to_authenticated_when_score_is_100() {
            // given
            bid.updateStatus(Status.IN_TRANSACTION);

            // when
            bid.inspect(100);

            // then
            assertThat(bid.getStatus()).isEqualTo(Status.AUTHENTICATED);
        }

        @Test
        @DisplayName("점수가 95 이상이면 상태가 ACCREDITED로 변경된다")
        void should_update_status_to_accredited_when_score_is_95_or_above() {
            // given
            bid.updateStatus(Status.IN_TRANSACTION);

            // when
            bid.inspect(95);

            // then
            assertThat(bid.getStatus()).isEqualTo(Status.ACCREDITED);
        }

        @Test
        @DisplayName("점수가 95 미만이면 상태가 AUTHENTICATED_FAILED로 변경되고 관련 입찰은 CANCELLED 된다")
        void should_update_status_to_authenticated_failed_and_cancel_related_bid_when_score_is_below_95() {
            // given
            Bid relatedBid = SELL_BID_IN_TRANSACTION.getBid();
            bid.relateBid(relatedBid);
            bid.updateStatus(Status.IN_TRANSACTION);

            // when
            bid.inspect(90);

            // then
            assertThat(bid.getStatus()).isEqualTo(Status.AUTHENTICATED_FAILED);
            assertThat(relatedBid.getStatus()).isEqualTo(Status.CANCELLED);
        }
    }

    @Nested
    @DisplayName("cancel 메서드는")
    class cancel {

        @Test
        @DisplayName("LIVE 상태의 입찰을 취소할 수 있다")
        void should_cancel_live_bid() {
            // when
            bid.cancel();

            // then
            assertThat(bid.getStatus()).isEqualTo(Status.CANCELLED);
        }

        @Test
        @DisplayName("자신이 IN_TRANSACTION 상태이고, 관련 입찰도 IN_TRANSACTION 상태인 입찰을 취소할 수 있다")
        void should_cancel_in_transaction_bid() {
            // given
            bid.updateStatus(Status.IN_TRANSACTION);
            Bid relatedBid = SELL_BID_IN_TRANSACTION.getBid();
            bid.relateBid(relatedBid);

            // when
            bid.cancel();

            // then
            assertThat(bid.getStatus()).isEqualTo(Status.CANCELLED);
            assertThat(relatedBid.getStatus()).isEqualTo(Status.CANCELLED);
        }

        @Test
        @DisplayName("자신이 IN_TRANSACTION 상태이고, 관련 입찰이 IN_TRANSACTION 상태가 아닌 경우 BadRequestException이 발생한다")
        void should_throw_BadRequestException_when_cancel_related_bid_not_in_transaction() {
            // given
            bid.updateStatus(Status.IN_TRANSACTION);
            Bid relatedBid = SELL_BID_AUTHENTICATED.getBid();
            bid.relateBid(relatedBid);

            // when
            BadRequestException result = assertThrows(BadRequestException.class,
                () -> bid.cancel());

            // then
            assertEquals(BidErrorCode.BID_CANNOT_CANCEL, result.getResponseCode());
        }

        @ParameterizedTest(name = "상태 값이 {0}일 때")
        @EnumSource(mode = Mode.INCLUDE, names = {"AUTHENTICATED", "ACCREDITED", "DELIVERING", "DELIVERED"})
        @DisplayName("입찰 취소가 불가능한 경우 BadRequestException이 발생한다")
        void should_throw_exception_when_cancelling_cancelled_bid(Status status) {
            // given
            bid.updateStatus(status);

            // when
            BadRequestException result = assertThrows(BadRequestException.class,
                () -> bid.cancel());

            // then
            assertEquals(BidErrorCode.BID_CANNOT_CANCEL, result.getResponseCode());
        }
    }

    private Bid bid;
    private Product product;
    private User user;
}
