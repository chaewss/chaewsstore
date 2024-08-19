package com.chaewsstore.app.apis.bid;

import static com.chaewsstore.core.domain.BidFixture.BID;
import static com.chaewsstore.core.domain.ProductFixture.PRODUCT;
import static com.chaewsstore.core.domain.UserFixture.BUYER;
import static com.chaewsstore.core.domain.UserFixture.SELLER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.chaewsstore.app.apis.bid.dto.TransactBidRequestDto;
import com.chaewsstore.app.apis.bid.usecase.BidUseCase;
import com.chaewsstore.app.config.DatabaseClearExtension;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.bid.BidService;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.product.ProductService;
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
import org.springframework.test.context.TestPropertySource;

@Slf4j
@ExtendWith(DatabaseClearExtension.class)
@TestPropertySource(properties = "SECRET_KEY=helloThisIsChaewsstoreSecretKeyAndItNeedsToBeLongerThan256Bits")
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
        seller = userService.create(SELLER.getUser());
        buyer = userService.create(BUYER.getUser());
        product = productService.create(PRODUCT.getProduct());
    }

    @Test
    @DisplayName("멀티 스레드 환경에서 판매 입찰에 대한 구매 입찰을 동시에 생성하는 경우 첫 번째 요청만 주문 생성이 보장된다")
    void succeed_to_create_order_in_multi_thread() throws InterruptedException {
        Bid liveSellBid = BID.getBidWithUserAndProductAndPriceAndBidTypeAndStatus(seller, product,
            600, BidType.SELL, Status.LIVE);
        bidService.create(liveSellBid);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        TransactBidRequestDto request = new TransactBidRequestDto(product.getId(),
            liveSellBid.getPrice());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    bidUseCase.transactSellBid(seller, request);
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
        Bid authenticatedSellBid = BID.getBidWithUserAndProductAndPriceAndBidTypeAndStatus(seller,
            product, 600, BidType.SELL, Status.AUTHENTICATED);
        bidService.create(authenticatedSellBid);

        Bid inTransactionBuyBid = BID.getBidWithUserAndProductAndPriceAndBidTypeAndStatus(buyer,
            product, 600, BidType.BUY, Status.IN_TRANSACTION);
        inTransactionBuyBid.relateBid(authenticatedSellBid);
        bidService.create(inTransactionBuyBid);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Long beforeSellerBalance = seller.getAccount();
        Long beforeBuyerBalance = buyer.getAccount();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i + 1;
            executor.submit(() -> {
                try {
                    log.info("스레드 {} 시작", threadId);
                    bidUseCase.depositBid(buyer, inTransactionBuyBid.getId());
                    successCount.getAndIncrement();
                    log.info("스레드 {} 성공", threadId);
                } catch (ObjectOptimisticLockingFailureException e) {
                    failCount.getAndIncrement();
                    log.info("스레드 {} 실패", threadId);
                } catch (Exception e) {
                    failCount.getAndIncrement();
                    log.info("스레드 {} 실패: {}", threadId, e.getMessage());
                } finally {
                    latch.countDown();
                    log.info("스레드 {} 완료", threadId);
                }
            });
        }

        latch.await();
        executor.shutdown();

        log.info("성공 스레드 수: {}", successCount.get());
        log.info("실패 스레드 수: {}", failCount.get());
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
