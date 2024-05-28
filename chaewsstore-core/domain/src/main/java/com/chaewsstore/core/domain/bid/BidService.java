package com.chaewsstore.core.domain.bid;

import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.core.domain.product.Product;
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
    public Bid create(Bid bid) {
        return bidRepository.save(bid);
    }

    @Transactional(readOnly = true)
    public Optional<Bid> readById(Long id) {
        return bidRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Boolean existsByProductAndBidder(Product product, Account account) {
        return bidRepository.existsByProductAndBidder(product, account);
    }

    @Transactional(readOnly = true)
    public List<ReadProductBidQueryDto> readAllByProduct(Product product, Pageable pageable) {
        return bidRepository.findAllByProduct(product, pageable);
    }

    @Transactional
    public void remove(Bid bid) {
        bidRepository.delete(bid);
    }
}
