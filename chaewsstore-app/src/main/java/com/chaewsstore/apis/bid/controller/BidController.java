package com.chaewsstore.apis.bid.controller;

import com.chaewsstore.apis.bid.dto.CreateBidRequestDto;
import com.chaewsstore.apis.bid.dto.ReadProductBidResponseDto;
import com.chaewsstore.apis.bid.dto.TransactBidRequestDto;
import com.chaewsstore.apis.bid.dto.UpdateBidRequestDto;
import com.chaewsstore.apis.bid.usecase.BidUseCase;
import com.chaewsstore.common.annotation.LoginUser;
import com.chaewsstore.core.domain.user.User;
import com.globalutils.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api")
public class BidController {

    private final BidUseCase bidUseCase;

    @GetMapping("/products/{productId}/bids")
    public SuccessResponse<Slice<ReadProductBidResponseDto>> readProductBids(
        @PathVariable Long productId, Pageable pageable) {
        return SuccessResponse.from(bidUseCase.readProductBidList(productId, pageable));
    }

    @PostMapping("/products/{productId}/bids")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> createBid(@LoginUser User user,
        @PathVariable Long productId, @RequestBody CreateBidRequestDto request) {
        bidUseCase.createBid(user, productId, request);
        return SuccessResponse.create();
    }

    @PostMapping("/bids/buy-now")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> transactSellBid(@LoginUser User user,
        @RequestBody TransactBidRequestDto request) {
        bidUseCase.transactSellBid(user, request);
        return SuccessResponse.create();
    }

    @PostMapping("/bids/sell-now")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> transactBuyBid(@LoginUser User user,
        @RequestBody TransactBidRequestDto request) {
        bidUseCase.transactBuyBid(user, request);
        return SuccessResponse.create();
    }

    @PutMapping("/bids/{bidId}")
    public SuccessResponse<Void> updateBid(@LoginUser User user, @PathVariable Long bidId,
        @RequestBody UpdateBidRequestDto request) {
        bidUseCase.updateBid(user, bidId, request);
        return SuccessResponse.create();
    }

    @DeleteMapping("/bids/{bidId}")
    public SuccessResponse<Void> deleteBid(@LoginUser User user,
        @PathVariable Long bidId) {
        bidUseCase.deleteBid(user, bidId);
        return SuccessResponse.create();
    }
}
