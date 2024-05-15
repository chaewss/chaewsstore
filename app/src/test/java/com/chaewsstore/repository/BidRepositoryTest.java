package com.chaewsstore.repository;

import static com.chaewsstore.entity.Role.ASSOCIATE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chaewsstore.TestConfig;
import com.chaewsstore.entity.Account;
import com.chaewsstore.entity.Bid;
import com.chaewsstore.entity.Product;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import(TestConfig.class)
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BidRepositoryTest {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void initData() {
        account = Account.create("abc@naver.com", "password1!", "닉네임", ASSOCIATE);
        product1 = Product.builder().name("상품1").price(5000).build();
        product2 = Product.builder().name("상품2").price(7000).build();
        bid = Bid.create(6000, product1, account);

        accountRepository.save(account);
        productRepository.saveAll(List.of(product1, product2));
        bidRepository.save(bid);
    }

    @Test
    @DisplayName("해당하는 상품 좋아요가 있는지 확인한다")
    void succeed_to_find_existing_bid_by_product_and_bidder() {
        boolean exists = bidRepository.existsByProductAndBidder(product1, account);
        assertTrue(exists);

        exists = bidRepository.existsByProductAndBidder(product2, account);
        assertFalse(exists);
    }

    Account account;
    Product product1;
    Product product2;
    Bid bid;
}
