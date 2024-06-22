package com.chaewsstore.apis.bid.controller;

import com.chaewsstore.apis.bid.dto.InspectBidProductRequestDto;
import com.chaewsstore.apis.bid.usecase.BidUseCase;
import com.globalutils.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/bids")
public class BidController {

    private final BidUseCase bidUseCase;

    @PatchMapping("/inspect/{bidId}")
    public SuccessResponse<Void> inspectBidProduct(@PathVariable Long bidId,
        @Valid @RequestBody InspectBidProductRequestDto request) {
        bidUseCase.inspectBidProduct(bidId, request);
        return SuccessResponse.create();
    }
}
