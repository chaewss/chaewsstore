package com.chaewsstore.core.domain.bid;

import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.user.User;
import com.globalutils.annotation.DomainService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@DomainService
public class BidService {

    private final BidRepository bidRepository;

    @Transactional
    public void flush() {
        bidRepository.flush();
    }

    @Transactional
    public Bid create(Bid bid) {
        return bidRepository.save(bid);
    }

    @Transactional(readOnly = true)
    public Optional<Bid> readById(Long id) {
        return bidRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Bid> readValidBid(Long productId, Integer price, BidType bidType) {
        return bidRepository.findFirstByProductIdAndPriceAndBidTypeAndStatusOrderByCreatedAtAsc(
            productId, price, bidType, Status.LIVE);
    }

    @Transactional(readOnly = true)
    public Optional<Bid> readLiveBidByProductAndBidderAndType(Product product, User user,
        BidType bidType) {
        return bidRepository.findByProductAndBidderAndStatusAndBidType(product, user, Status.LIVE,
            bidType);
    }

    @Transactional(readOnly = true)
    public List<ReadProductBidQueryDto> readAllByProduct(Product product, BidType bidType, Pageable pageable) {
        return bidRepository.findAllByProduct(product, bidType, pageable);
    }

    @Transactional
    public void remove(Bid bid) {
        bidRepository.delete(bid);
    }
}
