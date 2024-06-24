package com.chaewsstore.app.apis.bid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@Slf4j
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
        seller = User.builder()
            .username("seller@gmail.com")
            .password("aaaa1111!!")
            .nickname("seller")
            .account(20000L)
            .role(Role.ASSOCIATE)
            .isDeleted(false)
            .build();
        userService.create(seller);

        buyer = User.builder()
            .username("buyer@gmail.com")
            .password("aaaa1111!!")
            .nickname("buyer")
            .account(100000L)
            .role(Role.ASSOCIATE)
            .isDeleted(false)
            .build();
        userService.create(buyer);

        product = Product.builder()
            .name("상품1")
            .price(40000)
            .isDeleted(false)
            .build();
        productService.create(product);
    }

    @Test
    @DisplayName("멀티 스레드 환경에서 판매 입찰에 대한 구매 입찰을 동시에 생성하는 경우 첫 번째 요청만 주문 생성이 보장된다")
    void succeed_to_create_order_in_multi_thread() throws InterruptedException {
        Bid liveBuyBid = Bid.builder()
            .product(product)
            .bidder(buyer)
            .price(60000)
            .bidType(BidType.SELL)
            .status(Status.LIVE)
            .isDeleted(false)
            .build();
        bidService.create(liveBuyBid);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        TransactBidRequestDto request = new TransactBidRequestDto(product.getId(),
            liveBuyBid.getPrice());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    bidUseCase.transactSellBid(buyer, request);
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

    @Test
    @DisplayName("멀티 스레드 환경에서 구매자가 입찰 상품 금액을 동시에 입금하는 경우 첫 번째 요청의 입금 · 출금이 보장된다")
    void succeed_to_deposit__bid_in_multi_thread() throws InterruptedException {
        Bid authenticatedSellBid = Bid.builder()
            .product(product)
            .bidder(seller)
            .price(6000)
            .bidType(BidType.SELL)
            .status(Status.AUTHENTICATED)
            .isDeleted(false)
            .build();
        bidService.create(authenticatedSellBid);

        Bid inTransactionBuyBid = Bid.builder()
            .product(product)
            .bidder(buyer)
            .price(6000)
            .bidType(BidType.BUY)
            .status(Status.IN_TRANSACTION)
            .relatedBid(authenticatedSellBid)
            .isDeleted(false)
            .build();
        bidService.create(inTransactionBuyBid);
        authenticatedSellBid.relateBid(inTransactionBuyBid);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Long beforeSellerBalance = seller.getAccount();
        Long beforeBuyerBalance = buyer.getAccount();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    bidUseCase.depositBid(buyer, inTransactionBuyBid.getId());
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

        Long afterSellerBalance = userService.readByUsername(seller.getUsername()).get()
            .getAccount();
        Long afterBuyerBalance = userService.readByUsername(buyer.getUsername()).get().getAccount();
        assertAll(
            () -> assertThat(successCount.get()).isEqualTo(1),
            () -> assertThat(failCount.get()).isEqualTo(9),
            () -> assertThat(afterSellerBalance).isEqualTo(
                beforeSellerBalance + inTransactionBuyBid.getPrice()),
            () -> assertThat(afterBuyerBalance).isEqualTo(
                beforeBuyerBalance - inTransactionBuyBid.getPrice())
        );
    }

    User seller;
    User buyer;
    Product product;
}
