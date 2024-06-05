package com.chaewsstore.apis.product.dto;

import com.chaewsstore.core.domain.brand.Brand;
import com.chaewsstore.core.domain.product.Product;

public record CreateProductRequestDto(
    String name,
    Integer price,
    String brandName
) {

    public Product toEntity(Brand brand) {
        return Product.create(name, price, brand);
    }
}
