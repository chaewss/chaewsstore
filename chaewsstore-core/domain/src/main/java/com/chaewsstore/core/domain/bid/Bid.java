package com.chaewsstore.core.domain.bid;

import com.chaewsstore.core.domain.BaseTimeEntity;
import com.chaewsstore.core.domain.common.Status;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.user.User;
import com.globalutils.annotation.Generated;
import com.globalutils.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@SQLDelete(sql = "UPDATE bid SET is_deleted = true WHERE id = ? AND version = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Bid extends BaseTimeEntity {

    public enum BidType {
        SELL, BUY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id")
    private User bidder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(value = EnumType.STRING)
    private BidType bidType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_id")
    private Bid relatedBid;

    @Column(updatable = false)
    private LocalDateTime transactionAt;

    @Version
    private Long version;

    private Boolean isDeleted;

    @Builder
    public Bid(Long id, Integer price, Product product, User bidder, Status status, BidType bidType,
        Bid relatedBid, LocalDateTime transactionAt, Boolean isDeleted) {
        this.id = id;
        this.price = price;
        this.product = product;
        this.bidder = bidder;
        this.status = status;
        this.bidType = bidType;
        this.relatedBid = relatedBid;
        this.transactionAt = transactionAt;
        this.isDeleted = isDeleted;
    }

    public static Bid create(Integer price, Product product, User bidder, BidType bidType) {
        return Bid.builder()
            .price(price)
            .product(product)
            .bidder(bidder)
            .status(Status.LIVE)
            .bidType(bidType)
            .isDeleted(false)
            .build();
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }

    public static Bid transactSellBidAndCreateBuyBid(User user, Bid sellBid) {
        LocalDateTime transactionAt = LocalDateTime.now();
        sellBid.executeTransaction(transactionAt);
        return create(user, sellBid, Status.IN_TRANSACTION, BidType.BUY, transactionAt);
    }

    public static Bid transactBuyBidAndCreateSellBid(User user, Bid buyBid) {
        LocalDateTime transactionAt = LocalDateTime.now();
        buyBid.executeTransaction(transactionAt);
        return create(user, buyBid, Status.IN_TRANSACTION, BidType.SELL, transactionAt);
    }

    public void relateBid(Bid bid) {
        this.relatedBid = bid;
    }

    public void inspect(Integer score) {
        validateStatus(Status.IN_TRANSACTION, BidErrorCode.BID_NOT_IN_TRANSACTION);
        updateStatusForInspect(score);
    }

    public Long calculateFinalPrice(Integer price) {
        validateStatus(Status.IN_TRANSACTION, BidErrorCode.BID_NOT_IN_TRANSACTION);
        validateStatus(Status.AUTHENTICATED, Status.ACCREDITED, BidErrorCode.BID_NOT_INSPECT);
        return relatedBid.getStatus().equals(Status.ACCREDITED) ? Math.round(price * 0.85)
            : (long) price;
    }

    public void updateStatusAfterDeposit() {
        this.status = Status.DELIVERING;
        this.relatedBid.status = Status.FINISHED;
    }

    public void cancel() {
        if (isCancelable()) {
            updateStatusCancelled();
        } else {
            throw new BadRequestException(BidErrorCode.BID_CANNOT_CANCEL);
        }
    }

    private void validateStatus(Status expectedStatus, BidErrorCode errorCode) {
        if (this.status != expectedStatus) {
            throw new BadRequestException(errorCode);
        }
    }

    private void validateStatus(Status expectedStatus1, Status expectedStatus2,
        BidErrorCode errorCode) {
        if (this.relatedBid.getStatus() != expectedStatus1 && this.relatedBid.getStatus() != expectedStatus2) {
            throw new BadRequestException(errorCode);
        }
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    private void updateRelatedBidStatus(Status status) {
        Bid relatedBid = this.getRelatedBid();
        if (relatedBid != null) {
            relatedBid.updateStatus(status);
        }
    }

    private void updateStatusForInspect(Integer score) {
        if (score == 100) {
            this.status = Status.AUTHENTICATED;
        } else if (score >= 95) {
            this.status = Status.ACCREDITED;
        } else {
            this.status = Status.AUTHENTICATED_FAILED;
            this.relatedBid.status = Status.CANCELLED;
        }
    }

    private void updateStatusCancelled() {
        if (this.status == Status.IN_TRANSACTION) {
            updateRelatedBidStatus(Status.CANCELLED);
        }
        this.updateStatus(Status.CANCELLED);
    }

    private boolean isCancelable() {
        if (this.status == Status.LIVE) {
            return true;
        } else if (this.status == Status.IN_TRANSACTION) {
            Bid cancelRelatedBid = this.getRelatedBid();
            return cancelRelatedBid.getStatus() == Status.IN_TRANSACTION;
        }
        return false;
    }

    private static Bid create(User user, Bid relatedBid, Status status, BidType bidType,
        LocalDateTime transactionAt) {
        return Bid.builder()
            .price(relatedBid.getPrice())
            .product(relatedBid.getProduct())
            .bidder(user)
            .relatedBid(relatedBid)
            .status(status)
            .bidType(bidType)
            .transactionAt(transactionAt)
            .isDeleted(false)
            .build();
    }

    private void executeTransaction(LocalDateTime transactionAt) {
        this.status = Status.IN_TRANSACTION;
        this.transactionAt = transactionAt;
    }

    @Override
    @Generated
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bid bid)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), bid.getId());
    }

    @Override
    @Generated
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
