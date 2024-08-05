package com.chaewsstore.core.domain;

import static com.chaewsstore.core.domain.BrandFixture.BRAND1;
import static com.chaewsstore.core.domain.BrandFixture.BRAND2;

import com.chaewsstore.core.domain.brand.Brand;
import com.chaewsstore.core.domain.product.Product;

public enum ProductFixture {

    PRODUCT1(1L, "product1", 600, BRAND1.getBrand(), false),
    PRODUCT2(2L, "product2", 800, BRAND1.getBrand(), false),
    PRODUCT3(3L, "product3", 1000, BRAND2.getBrand(), false),
    NEW_PRODUCT(4L, "newProduct", 700, BRAND1.getBrand(), false),
    ;

    private final Long id;
    private final String name;
    private final Integer price;
    private final Brand brand;
    private final Boolean isDeleted;


    ProductFixture(Long id, String name, Integer price, Brand brand, Boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.isDeleted = isDeleted;
    }

    public Product getProduct() {
        return Product.builder()
            .id(id)
            .name(name)
            .price(price)
            .brand(brand)
            .isDeleted(isDeleted)
            .build();
    }

    public Product getProductWithBrand(Brand brand) {
        return Product.builder()
            .id(id)
            .name(name)
            .price(price)
            .brand(brand)
            .isDeleted(isDeleted)
            .build();
    }
}
