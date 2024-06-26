package com.chaewsstore.core.domain.product;

import com.chaewsstore.core.domain.BaseTimeEntity;
import com.chaewsstore.core.domain.brand.Brand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private Boolean isDeleted;

    @Builder
    public Product(Long id, String name, Integer price, Brand brand, Boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.isDeleted = isDeleted;
    }

    public static Product create(String name, Integer price, Brand brand) {
        return Product.builder()
            .name(name)
            .price(price)
            .brand(brand)
            .isDeleted(false)
            .build();
    }

    public void updateProduct(String name, Integer price, Brand brand) {
        this.name = name;
        this.price = price;
        this.brand = brand;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product product)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), product.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}