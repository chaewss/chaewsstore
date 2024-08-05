package com.chaewsstore.core.domain.bid;

import static org.assertj.core.api.Assertions.assertThat;

import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.chaewsstore.core.domain.brand.Brand;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.config.TestConfig;
import com.chaewsstore.core.domain.product.Product;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class BidCustomRepositoryImplTest {

    @Autowired
    private BidCustomRepositoryImpl bidCustomRepositoryImpl;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Brand brand = Brand.builder()
            .name("Adidas")
            .build();
        entityManager.persist(brand);

        product = Product.builder()
            .name("Yeezy Foam RNNR")
            .price(129000)
            .brand(brand)
            .build();
        entityManager.persist(product);

        pageable = PageRequest.of(0, 20);

        // Transaction 상태 입찰
        for (int i = 1; i <= 25; i++) {
            Bid bid = Bid.builder()
                .product(product)
                .price(i)
                .transactionAt(LocalDateTime.now().minusDays(i))
                .status(Status.IN_TRANSACTION)
                .isDeleted(false)
                .build();
            entityManager.persist(bid);
        }
        // Live 상태의 구매 입찰
        for (int i = 10; i > 5; i--) {
            Bid bid = Bid.builder()
                .product(product)
                .price(i)
                .bidType(BidType.BUY)
                .status(Status.LIVE)
                .isDeleted(false)
                .build();
            entityManager.persist(bid);
        }
        // Live 상태의 판매 입찰
        for (int i = 1; i <= 10; i++) {
            Bid bid = Bid.builder()
                .product(product)
                .price(i)
                .bidType(BidType.SELL)
                .status(Status.LIVE)
                .isDeleted(false)
                .build();
            entityManager.persist(bid);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("product와 bidType에 따라 입찰 정보를 페이징 처리하여 조회한다")
    class succeed_to_find_all_bids_by_product {
        @Test
        @DisplayName("bidType이 null인 경우 거래일 내림차순 정렬")
        void without_bid_type() {
            List<ReadProductBidQueryDto> result = bidCustomRepositoryImpl.findAllByProduct(product,
                null, pageable);

            assertThat(result).hasSize(20);
            assertThat(result.get(0).bidPrice()).isEqualTo(1);
        }

        @Test
        @DisplayName("bidType이 BUY인 경우 가격 내림차순 정렬")
        void with_bid_type_BUY() {
            List<ReadProductBidQueryDto> result = bidCustomRepositoryImpl.findAllByProduct(product,
                BidType.BUY, pageable);

            assertThat(result).hasSize(5);
            assertThat(result.get(0).bidPrice()).isEqualTo(10);
        }

        @Test
        @DisplayName("bidType이 SELL인 경우 가격 오름차순 정렬")
        void with_bid_type_SELL() {
            List<ReadProductBidQueryDto> result = bidCustomRepositoryImpl.findAllByProduct(product,
                BidType.SELL, pageable);

            assertThat(result).hasSize(10);
            assertThat(result.get(0).bidPrice()).isEqualTo(1);
        }
    }

    private Product product;
    private Pageable pageable;
}
