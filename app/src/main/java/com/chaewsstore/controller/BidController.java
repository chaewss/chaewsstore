package com.chaewsstore.controller;

import com.chaewsstore.dto.bid.CreateBidRequestDto;
import com.chaewsstore.entity.Account;
import com.chaewsstore.service.BidService;
import com.chaewsstore.util.LoginAccount;
import com.chaewsstore.util.ResponseCode;
import com.chaewsstore.util.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api")
public class BidController {

    private final BidService bidService;

    @PostMapping("/products/{productId}/bids")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Void> createBid(@LoginAccount Account account, @PathVariable Long productId,
        @RequestBody CreateBidRequestDto request) {
        bidService.createBid(account, productId, request);
        return ResponseData.of(ResponseCode.CREATE_BID_SUCCESS);
    }
}
