package com.chaewsstore.admin.apis.bid.usecase;

import static com.chaewsstore.admin.common.exception.ExceptionConstants.NOT_FOUND_BID;

import com.chaewsstore.admin.apis.bid.dto.InspectBidProductRequestDto;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.bid.BidService;
import com.globalutils.annotation.UseCase;
import com.globalutils.exception.BadRequestException;
import com.globalutils.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class BidUseCase {

    private final BidService bidService;

    /**
     * 특정 입찰에 대해 검수 점수를 기준으로 상태를 업데이트한다.
     *
     * @param bidId   검수할 판매 입찰 ID
     * @param request 검수 요청 정보
     * @throws NotFoundException   입찰이 존재하지 않는 경우
     * @throws BadRequestException 입찰 상태가 거래중(IN_TRANSACTION)이 아닌 경우
     */
    @Transactional
    public void inspectBidProduct(Long bidId, InspectBidProductRequestDto request) {
        Bid sellBid = bidService.readById(bidId).orElseThrow(() -> NOT_FOUND_BID);
        sellBid.inspect(request.score());
    }
}
