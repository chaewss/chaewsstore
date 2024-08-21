package com.chaewsstore.core.domain.bid;

import static com.chaewsstore.core.domain.BidFixture.ANOTHER_USER_BID;
import static com.chaewsstore.core.domain.BidFixture.BUY_BID_LIVE;
import static com.chaewsstore.core.domain.BidFixture.SELL_BID_LIVE;
import static com.chaewsstore.core.domain.ProductFixture.PRODUCT1;
import static com.chaewsstore.core.domain.UserFixture.ANOTHER_USER;
import static com.chaewsstore.core.domain.UserFixture.USER;
import static org.assertj.core.api.Assertions.assertThat;

import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.brand.Brand;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.config.TestConfig;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.user.User;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class BidRepositoryTest {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        user = entityManager.merge(USER.getUser());
        anotherUser = entityManager.merge(ANOTHER_USER.getUser());

        Brand brand = Brand.builder().name("브랜드1").build();
        entityManager.persist(brand);

        product = entityManager.merge(PRODUCT1.getProductWithBrand(brand));

        sellBid = bidRepository.save(
            SELL_BID_LIVE.getBidWithUserAndProductAndPriceAndBidTypeAndStatus(user, product, 800,
                BidType.SELL, Status.LIVE));
        oldBuyBid = bidRepository.save(
            BUY_BID_LIVE.getBidWithUserAndProductAndPriceAndBidTypeAndStatus(user, product, 800,
                BidType.BUY, Status.LIVE));
        newBuyBid = bidRepository.save(
            ANOTHER_USER_BID.getBidWithUserAndProductAndPriceAndBidTypeAndStatus(anotherUser,
                product, 800, BidType.BUY, Status.LIVE));
    }

    @Test
    @DisplayName("상품, 입찰 가격, 입찰 타입, 입찰 상태 조건에 맞는 가장 오래된 입찰을 조회한다")
    void succeed_to_find_first_bid_by_product_and_price_and_bid_type_and_status_order_by_created_at_asc() {
        Optional<Bid> foundBid = bidRepository.findFirstByProductAndPriceAndBidTypeAndStatusOrderByCreatedAtAsc(
            product, 800, BidType.BUY, Status.LIVE);

        assertThat(foundBid)
            .isPresent()
            .contains(oldBuyBid);
    }

    @Test
    @DisplayName("상품, 입찰자, 입찰 상태, 입찰 타입 조건에 맞는 입찰을 조회한다")
    void succeed_to_find_bid_by_product_and_bidder_and_status_and_bid_type() {
        Optional<Bid> foundBid = bidRepository.findByProductAndBidderAndStatusAndBidType(
            product, anotherUser, Status.LIVE, BidType.BUY);

        assertThat(foundBid)
            .isPresent()
            .contains(newBuyBid);
    }

    User user;
    User anotherUser;
    Product product;
    Bid oldBuyBid;
    Bid newBuyBid;
    Bid sellBid;
}
