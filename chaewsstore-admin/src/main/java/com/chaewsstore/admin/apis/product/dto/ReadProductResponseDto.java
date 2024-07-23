package com.chaewsstore.admin.apis.product.dto;

import com.chaewsstore.core.domain.product.Product;

public record ReadProductResponseDto(
    Long id,
    String name,
    Integer price,
    String brandName
) {

    public static ReadProductResponseDto of(Product product) {
        return new ReadProductResponseDto(product.getId(), product.getName(), product.getPrice(),
            product.getBrand().getName());
    }
}
