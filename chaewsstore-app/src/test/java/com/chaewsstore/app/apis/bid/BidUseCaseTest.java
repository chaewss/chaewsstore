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

import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.bid.BidService;
import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.apis.bid.usecase.BidUseCase;
import com.chaewsstore.apis.bid.dto.CreateBidRequestDto;
import com.chaewsstore.apis.bid.dto.ReadProductBidResponseDto;
import com.chaewsstore.apis.bid.dto.UpdateBidRequestDto;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.product.Product;
import com.globalutils.exception.DuplicateException;
import com.globalutils.exception.ForbiddenException;
import com.globalutils.exception.NotFoundException;
import com.chaewsstore.core.domain.product.ProductService;
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
class BidUseCaseTest {

    @InjectMocks
    private BidUseCase bidUseCase;

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

    @Test
    @DisplayName("입찰을 정상적으로 추가한다")
    void succeed_to_create_bid() {
        CreateBidRequestDto request = new CreateBidRequestDto(6000);

        given(productService.readById(anyLong())).willReturn(Optional.of(product));
        given(bidService.existsByProductAndBidder(any(), any())).willReturn(false);
        given(bidService.create(any())).willReturn(any());

        bidUseCase.createBid(user, 1L, request);

        then(productService).should(times(1)).readById(anyLong());
        then(bidService).should(times(1)).existsByProductAndBidder(any(), any());
        then(bidService).should(times(1)).create(any());
    }

    @Test
    @DisplayName("상품이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_create_bid_but_product_does_not_exist() {
        CreateBidRequestDto request = new CreateBidRequestDto(6000);

        given(productService.readById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bidUseCase.createBid(user, 999L, request));

        then(productService).should(times(1)).readById(anyLong());
    }

    @Test
    @DisplayName("사용자가 해당 상품의 입찰을 이미 생성한 경우 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_bid_but_user_has_already_create_bid() {
        CreateBidRequestDto request = new CreateBidRequestDto(6000);

        given(productService.readById(anyLong())).willReturn(Optional.of(product));
        given(bidService.existsByProductAndBidder(any(), any())).willReturn(true);

        assertThrows(DuplicateException.class, () -> bidUseCase.createBid(user, 1L, request));

        then(productService).should(times(1)).readById(anyLong());
        then(bidService).should(times(1)).existsByProductAndBidder(any(), any());
    }

    @Test
    @DisplayName("입찰을 정상적으로 수정한다")
    void succeed_to_update_bid() {
        UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

        given(bidService.readById(anyLong())).willReturn(Optional.of(bid));

        bidUseCase.updateBid(user, 1L, request);

        assertEquals(request.price(), bid.getPrice());
        then(bidService).should(times(1)).readById(anyLong());
    }

    @Test
    @DisplayName("입찰이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFountException_when_update_bid_but_bid_does_not_exist() {
        UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

        given(bidService.readById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bidUseCase.updateBid(user, 1L, request));

        then(bidService).should(times(1)).readById(anyLong());
    }

    @Test
    @DisplayName("입찰자가 아닌 사용자가 입찰 수정을 시도할 경우 ForbiddenException이 발생한다")
    void should_throw_ForbiddenException_when_update_bid_but_user_is_not_bidder() {
        UpdateBidRequestDto request = new UpdateBidRequestDto(7000);

        given(bidService.readById(anyLong())).willReturn(Optional.of(bid));

        assertThrows(ForbiddenException.class, () -> bidUseCase.updateBid(anotherUser, 1L, request));

        then(bidService).should(times(1)).readById(anyLong());
    }

    @Test
    @DisplayName("특정 입찰을 정상적으로 삭제한다")
    void succeed_to_delete_bid() {
        given(bidService.readById(any())).willReturn(Optional.of(bid));

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
        given(bidService.readById(anyLong())).willReturn(Optional.of(bid));

        assertThrows(ForbiddenException.class, () -> bidUseCase.deleteBid(anotherUser, 1L));

        then(bidService).should(times(1)).readById(anyLong());
    }

    User user = User.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("aaaa1111!!")
        .nickname("닉네임")
        .build();
    User anotherUser = User.builder()
        .id(2L)
        .username("another@gmail.com")
        .password("aaaa1111!!")
        .nickname("닉네임2")
        .build();

    Product product = Product.builder().build();
    Bid bid = Bid.builder()
        .bidder(user)
        .price(30)
        .build();

    List<ReadProductBidQueryDto> productBidQueryDtoList = List.of(
        new ReadProductBidQueryDto(7000, 1L), new ReadProductBidQueryDto(8000, 3L),
        new ReadProductBidQueryDto(1000, 1L));
}
