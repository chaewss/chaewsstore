package com.chaewsstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private Integer price;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Account seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Account buyer;

    @Builder
    public Receipt(Long id, Integer price, Account seller, Account buyer) {
        this.id = id;
        this.price = price;
        this.seller = seller;
        this.buyer = buyer;
    }
}
