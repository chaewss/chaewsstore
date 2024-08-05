package com.chaewsstore.core.domain;

import com.chaewsstore.core.domain.brand.Brand;

public enum BrandFixture {

    BRAND1(1L, "brand1"),
    BRAND2(2L, "brand2"),
    ;

    private final Long id;
    private final String name;


    BrandFixture(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Brand getBrand() {
        return Brand.builder()
            .id(id)
            .name(name)
            .build();
    }
}
