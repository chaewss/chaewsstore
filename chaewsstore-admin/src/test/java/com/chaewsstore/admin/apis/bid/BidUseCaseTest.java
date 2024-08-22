package com.chaewsstore.admin.apis.bid;

import static com.chaewsstore.core.domain.BidFixture.BUY_BID_IN_TRANSACTION;
import static com.chaewsstore.core.domain.BidFixture.SELL_BID_IN_TRANSACTION;
import static com.chaewsstore.core.domain.BidFixture.SELL_BID_LIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.chaewsstore.admin.apis.bid.dto.InspectBidProductRequestDto;
import com.chaewsstore.admin.apis.bid.usecase.BidUseCase;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.bid.BidErrorCode;
import com.chaewsstore.core.domain.bid.BidService;
import com.chaewsstore.core.domain.common.Status;
import com.globalutils.exception.BadRequestException;
import com.globalutils.exception.NotFoundException;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("BidUseCase 클래스")
@ExtendWith(MockitoExtension.class)
class BidUseCaseTest {

    @InjectMocks
    private BidUseCase bidUseCase;

    @Mock
    private BidService bidService;

    @Nested
    @DisplayName("inspectBidProduct 메서드는")
    class inspect_bid_product {

        @ParameterizedTest(name = "score 값이 {0}일 때 상태: {1}")
        @MethodSource("bidProductInspect")
        @DisplayName("입찰 상품 검수 점수를 기준으로 입찰 상태를 업데이트한다")
        void succeed_to_inspect_bid_product(int score, Status status) {
            Bid sellBid = SELL_BID_IN_TRANSACTION.getBid();
            sellBid.relateBid(BUY_BID_IN_TRANSACTION.getBid());
            InspectBidProductRequestDto request = new InspectBidProductRequestDto(score);

            // given
            given(bidService.readById(any())).willReturn(Optional.of(sellBid));

            // when
            bidUseCase.inspectBidProduct(anyLong(), request);

            // then
            then(bidService).should(times(1)).readById(any());
            if (score == 100) {
                assertEquals(Status.AUTHENTICATED, sellBid.getStatus());
            } else if (score >= 95) {
                assertEquals(Status.ACCREDITED, sellBid.getStatus());
            } else {
                assertEquals(Status.AUTHENTICATED_FAILED, sellBid.getStatus());
                assertEquals(Status.CANCELLED, sellBid.getRelatedBid().getStatus());
            }
        }

        static Stream<Arguments> bidProductInspect() {
            return Stream.of(
                arguments(100, Status.AUTHENTICATED),
                arguments(95, Status.ACCREDITED),
                arguments(50, Status.AUTHENTICATED_FAILED)
            );
        }

        @Test
        @DisplayName("입찰이 존재하지 않는 경우 NotFoundException이 발생한다")
        void should_throw_NotFountException_when_inspect_bid_product_but_bid_does_not_exist() {
            Long bidId = 100L;
            InspectBidProductRequestDto request = new InspectBidProductRequestDto(100);

            // given
            given(bidService.readById(bidId)).willReturn(Optional.empty());

            // when
            NotFoundException result = assertThrows(NotFoundException.class,
                () -> bidUseCase.inspectBidProduct(bidId, request));

            // then
            then(bidService).should(times(1)).readById(any());
            assertEquals(BidErrorCode.NOT_FOUND_BID, result.getResponseCode());
        }

        @Test
        @DisplayName("입찰 상태가 거래중(IN_TRANSACTION)이 아닌 경우 NotFoundException이 발생한다")
        void should_throw_BadRequestException_when_inspect_bid_product_but_bid_status_is_not_in_transaction() {
            Bid notInTransactionBid = SELL_BID_LIVE.getBid();
            Long notInTransactionBidId = notInTransactionBid.getId();
            InspectBidProductRequestDto request = new InspectBidProductRequestDto(100);

            // given
            given(bidService.readById(notInTransactionBidId)).willReturn(
                Optional.of(notInTransactionBid));

            // when
            BadRequestException result = assertThrows(BadRequestException.class,
                () -> bidUseCase.inspectBidProduct(notInTransactionBidId, request));

            // then
            then(bidService).should(times(1)).readById(any());
            assertEquals(BidErrorCode.BID_NOT_IN_TRANSACTION, result.getResponseCode());
        }
    }
}
