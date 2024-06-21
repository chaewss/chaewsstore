package com.chaewsstore.app.apis.bid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.chaewsstore.apis.bid.dto.TransactBidRequestDto;
import com.chaewsstore.apis.bid.usecase.BidUseCase;
import com.chaewsstore.app.config.DatabaseClearExtension;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.bid.BidService;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.product.ProductService;
import com.chaewsstore.core.domain.user.Role;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.user.UserService;
import com.globalutils.exception.NotFoundException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest
class BidUseCaseIntegrationTest {

    @Autowired
    private BidUseCase bidUseCase;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private BidService bidService;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .username("username2@gmail.com")
            .password("aaaa1111!!")
            .nickname("nickname2")
            .account(0L)
            .role(Role.ASSOCIATE)
            .isDeleted(false)
            .build();
        userService.create(user);

        product = Product.builder()
            .name("상품1")
            .price(40000)
            .isDeleted(false)
            .build();
        productService.create(product);

        bid = Bid.builder()
            .product(product)
            .bidder(user)
            .price(60000)
            .bidType(BidType.SELL)
            .status(Status.LIVE)
            .isDeleted(false)
            .build();
        bidService.create(bid);
    }

    @Test
    @DisplayName("멀티 스레드 환경에서 판매 입찰에 대한 구매 입찰을 동시에 생성하는 경우 첫 번째 요청만 주문 생성이 보장된다")
    void succeed_to_create_order_in_multi_thread() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        TransactBidRequestDto request = new TransactBidRequestDto(product.getId(), bid.getPrice());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    bidUseCase.transactSellBid(user, request);
                    successCount.getAndIncrement();
                } catch (ObjectOptimisticLockingFailureException e) {
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertAll(
            () -> assertThat(successCount.get()).isEqualTo(1),
            () -> assertThat(failCount.get()).isEqualTo(9)
        );
    }

    User user;
    Product product;
    Bid bid;
}
