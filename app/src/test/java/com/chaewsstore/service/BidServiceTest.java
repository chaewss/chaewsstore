package com.chaewsstore.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.chaewsstore.dto.bid.CreateBidRequestDto;
import com.chaewsstore.entity.Account;
import com.chaewsstore.entity.Product;
import com.chaewsstore.exception.DuplicateException;
import com.chaewsstore.exception.NotFoundException;
import com.chaewsstore.repository.BidRepository;
import com.chaewsstore.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @InjectMocks
    private BidService bidService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BidRepository bidRepository;

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

    Account account = Account.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("aaaa1111!!")
        .nickname("닉네임")
        .build();

    Product product = Product.builder().build();
}
