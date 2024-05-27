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

import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.apis.bid.service.BidService;
import com.chaewsstore.apis.bid.dto.CreateBidRequestDto;
import com.chaewsstore.apis.bid.dto.ReadProductBidResponseDto;
import com.chaewsstore.apis.bid.dto.UpdateBidRequestDto;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.bid.BidRepository;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.product.ProductRepository;
import com.chaewsstore.core.common.exception.DuplicateException;
import com.chaewsstore.core.common.exception.ForbiddenException;
import com.chaewsstore.core.common.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @InjectMocks
    private BidService bidService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BidRepository bidRepository;

    @Test
    @DisplayName("상품에 대한 입찰 목록을 조회한다")
    void succeed_to_read_product_bid_list() {
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(bidRepository.findAllByProduct(product, Pageable.unpaged())).willReturn(
            productBidQueryDtoList);

        Slice<ReadProductBidResponseDto> result = bidService.readProductBidList(anyLong(),
            Pageable.unpaged());

        assertNotNull(result);
        assertEquals(productBidQueryDtoList.size(), result.getContent().size());
        assertFalse(result.hasNext());
        then(productRepository).should(times(1)).findById(anyLong());
        then(bidRepository).should(times(1)).findAllByProduct(any(), any());
    }

    @Test
    @DisplayName("상품이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_read_product_bid_list_but_product_does_not_exist() {
        given(productRepository.findById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
            () -> bidService.readProductBidList(999L, Pageable.unpaged()));

        then(productRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("입찰을 정상적으로 추가한다")
    void succeed_to_create_bid() {
        CreateBidRequestDto request = new CreateBidRequestDto(6000);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(bidRepository.existsByProductAndBidder(any(), any())).willReturn(false);
        given(bidRepository.save(any())).willReturn(any());

        bidService.createBid(account, 1L, request);

        then(productRepository).should(times(1)).findById(anyLong());
        then(bidRepository).should(times(1)).existsByProductAndBidder(any(), any());
        then(bidRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("상품이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_create_bid_but_product_does_not_exist() {
        CreateBidRequestDto request = new CreateBidRequestDto(6000);

        given(productRepository.findById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bidService.createBid(account, 999L, request));

        then(productRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("사용자가 해당 상품의 입찰을 이미 생성한 경우 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_bid_but_account_has_already_create_bid() {
        CreateBidRequestDto request = new CreateBidRequestDto(6000);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(bidRepository.existsByProductAndBidder(any(), any())).willReturn(true);

        assertThrows(DuplicateException.class, () -> bidService.createBid(account, 1L, request));

        then(productRepository).should(times(1)).findById(anyLong());
        then(bidRepository).should(times(1)).existsByProductAndBidder(any(), any());
    }

    @Test
    @DisplayName("입찰을 정상적으로 수정한다")
    void succeed_to_update_bid() {
        UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

        given(bidRepository.findById(anyLong())).willReturn(Optional.of(bid));

        bidService.updateBid(account, 1L, request);

        assertEquals(request.price(), bid.getPrice());
        then(bidRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("입찰이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFountException_when_update_bid_but_bid_does_not_exist() {
        UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

        given(bidRepository.findById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bidService.updateBid(account, 1L, request));

        then(bidRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("입찰자가 아닌 사용자가 입찰 수정을 시도할 경우 ForbiddenException이 발생한다")
    void should_throw_ForbiddenException_when_update_bid_but_account_is_not_bidder() {
        UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

        given(bidRepository.findById(anyLong())).willReturn(Optional.of(bid));

        assertThrows(ForbiddenException.class, () -> bidService.updateBid(anotherAccount, 1L, request));

        then(bidRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("특정 입찰을 정상적으로 삭제한다")
    void succeed_to_delete_bid() {
        given(bidRepository.findById(any())).willReturn(Optional.of(bid));

        bidService.deleteBid(account, 1L);

        then(bidRepository).should(times(1)).findById(anyLong());
        then(bidRepository).should(times(1)).delete(any());
    }

    @Test
    @DisplayName("입찰이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFountException_when_delete_bid_but_bid_does_not_exist() {
        given(bidRepository.findById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bidService.deleteBid(account, 1L));

        then(bidRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("입찰자가 아닌 사용자가 입찰 삭제를 시도할 경우 ForbiddenException이 발생한다")
    void should_throw_ForbiddenException_when_delete_bid_but_account_is_not_bidder() {
        given(bidRepository.findById(anyLong())).willReturn(Optional.of(bid));

        assertThrows(ForbiddenException.class, () -> bidService.deleteBid(anotherAccount, 1L));

        then(bidRepository).should(times(1)).findById(anyLong());
    }

    Account account = Account.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("aaaa1111!!")
        .nickname("닉네임")
        .build();
    Account anotherAccount = Account.builder()
        .id(2L)
        .username("another@gmail.com")
        .password("aaaa1111!!")
        .nickname("닉네임2")
        .build();

    Product product = Product.builder().build();
    Bid bid = Bid.builder()
        .bidder(account)
        .price(30)
        .build();

    List<ReadProductBidQueryDto> productBidQueryDtoList = List.of(
        new ReadProductBidQueryDto(7000, 1L), new ReadProductBidQueryDto(8000, 3L),
        new ReadProductBidQueryDto(1000, 1L));
}
