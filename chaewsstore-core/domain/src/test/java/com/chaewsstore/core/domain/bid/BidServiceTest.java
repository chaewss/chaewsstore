package com.chaewsstore.core.domain.bid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("BidService 클래스")
@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @Mock
    private BidRepository bidRepository;

    @InjectMocks
    private BidService bidService;

    @BeforeEach
    void setUp() {
        bidPrice = 500;
        bid = Bid.builder().build();
        product = Product.builder().build();
        user = User.builder().build();
        bidType = BidType.BUY;
    }

    @Test
    @DisplayName("입찰 레포지토리를 flush 한다")
    void should_flush_bid_repository() {
        bidService.flush();
        then(bidRepository).should(times(1)).flush();
    }

    @Test
    @DisplayName("입찰을 생성한다")
    void should_create_bid() {
        given(bidRepository.save(any(Bid.class))).willReturn(bid);

        Bid result = bidService.create(bid);

        then(bidRepository).should(times(1)).save(bid);
        assertEquals(bid, result);
    }

    @Test
    @DisplayName("아이디로 입찰을 조회한다")
    void should_read_bid_by_id() {
        Long bidId = 1L;
        given(bidRepository.findById(bidId)).willReturn(Optional.of(bid));

        Optional<Bid> result = bidService.readById(bidId);

        then(bidRepository).should(times(1)).findById(bidId);
        assertTrue(result.isPresent());
        assertEquals(bid, result.get());
    }

    @Test
    @DisplayName("상품 아이디, 입찰가, 입찰 타입 조건에 맞는 LIVE 상태인 입찰을 오래된 생성일 순으로 조회한다")
    void should_read_valid_bid() {
        given(bidRepository.findFirstByProductAndPriceAndBidTypeAndStatusOrderByCreatedAtAsc(
            product, bidPrice, bidType, Status.LIVE))
            .willReturn(Optional.of(bid));

        Optional<Bid> result = bidService.readFirstValidBid(product, bidPrice, bidType);

        then(bidRepository).should(times(1))
            .findFirstByProductAndPriceAndBidTypeAndStatusOrderByCreatedAtAsc(
                product, bidPrice, bidType, Status.LIVE);
        assertTrue(result.isPresent());
        assertEquals(bid, result.get());
    }

    @Test
    @DisplayName("상품, 입찰자, 입찰 타입에 따른 LIVE 상태인 입찰을 조회한다")
    void should_read_live_bid_by_product_and_bidder_and_type() {
        given(bidRepository.findByProductAndBidderAndStatusAndBidType(
            product, user, Status.LIVE, bidType))
            .willReturn(Optional.of(bid));

        Optional<Bid> result = bidService.readLiveBidByProductAndBidderAndType(product, user,
            bidType);

        then(bidRepository).should(times(1))
            .findByProductAndBidderAndStatusAndBidType(product, user, Status.LIVE, bidType);
        assertTrue(result.isPresent());
        assertEquals(bid, result.get());
    }

    @Test
    @DisplayName("상품과 입찰 타입에 따른 모든 입찰을 페이지네이션으로 조회한다")
    void should_read_all_bids_by_product() {
        Pageable pageable = PageRequest.of(0, 20);
        List<ReadProductBidQueryDto> readProductBidQueryDtoList = List.of(
            new ReadProductBidQueryDto(bidPrice, LocalDateTime.now()));

        given(bidRepository.findAllByProduct(product, bidType, pageable))
            .willReturn(readProductBidQueryDtoList);

        List<ReadProductBidQueryDto> result = bidService.readAllByProduct(product, bidType,
            pageable);

        then(bidRepository).should(times(1)).findAllByProduct(product, bidType, pageable);
        assertEquals(readProductBidQueryDtoList, result);
    }

    @Test
    @DisplayName("입찰을 삭제한다")
    void should_remove_bid() {
        bidService.remove(bid);
        then(bidRepository).should(times(1)).delete(bid);
    }

    private Bid bid;
    private Product product;
    private User user;
    private Integer bidPrice;
    private BidType bidType;
}
