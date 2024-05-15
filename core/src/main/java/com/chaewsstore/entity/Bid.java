package com.chaewsstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@SQLDelete(sql = "UPDATE bid SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "bidder_id"})})
public class Bid extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "bidder_id")
    private Account bidder;

    private Boolean isSold;

    private Boolean isDeleted;

    @Builder
    public Bid(Long id, Integer price, Product product, Account bidder, Boolean isSold, Boolean isDeleted) {
        this.id = id;
        this.price = price;
        this.product = product;
        this.bidder = bidder;
        this.isSold = isSold;
        this.isDeleted = isDeleted;
    }
    
    public static Bid create(Integer price, Product product, Account bidder) {
        return Bid.builder()
            .price(price)
            .product(product)
            .bidder(bidder)
            .isSold(false)
            .isDeleted(false)
            .build();
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }
}
