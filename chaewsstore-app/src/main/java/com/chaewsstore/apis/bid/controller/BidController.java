package com.chaewsstore.apis.bid.controller;

import com.chaewsstore.apis.bid.dto.CreateBidRequestDto;
import com.chaewsstore.apis.bid.dto.ReadProductBidResponseDto;
import com.chaewsstore.apis.bid.dto.UpdateBidRequestDto;
import com.chaewsstore.common.annotation.LoginAccount;
import com.chaewsstore.apis.bid.usecase.BidUseCase;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.common.util.ResponseCode;
import com.chaewsstore.core.common.util.ResponseData;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("api")
public class BidController {

    private final BidUseCase bidUseCase;

    @GetMapping("/products/{productId}/bids")
    public ResponseData<Slice<ReadProductBidResponseDto>> readProductBids(
        @PathVariable Long productId, Pageable pageable) {
        return ResponseData.of(ResponseCode.READ_PRODUCT_BID_SUCCESS,
            bidUseCase.readProductBidList(productId, pageable));
    }

    @PostMapping("/products/{productId}/bids")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Void> createBid(@LoginAccount Account account, @PathVariable Long productId,
        @RequestBody CreateBidRequestDto request) {
        bidUseCase.createBid(account, productId, request);
        return ResponseData.from(ResponseCode.CREATE_BID_SUCCESS);
    }

    @PutMapping("/bids/{bidId}")
    public ResponseData<Void> updateBid(@LoginAccount Account account, @PathVariable Long bidId,
        @RequestBody UpdateBidRequestDto request) {
        bidUseCase.updateBid(account, bidId, request);
        return ResponseData.from(ResponseCode.UPDATE_BID_SUCCESS);
    }

    @DeleteMapping("/bids/{bidId}")
    public ResponseData<Void> deleteBid(@LoginAccount Account account, @PathVariable Long bidId) {
        bidUseCase.deleteBid(account, bidId);
        return ResponseData.from(ResponseCode.DELETE_BID_SUCCESS);
    }
}
