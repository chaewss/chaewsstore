package com.chaewsstore.core.domain.receipt;

import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.bid.Bid;
import com.chaewsstore.core.domain.BaseTimeEntity;
import com.chaewsstore.core.domain.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Receipt extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "bid_id")
    private Bid bid;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @Builder
    public Receipt(Long id, Product product, Bid bid, User buyer) {
        this.id = id;
        this.product = product;
        this.bid = bid;
        this.buyer = buyer;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Receipt receipt)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), receipt.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
