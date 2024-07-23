package com.chaewsstore.admin.apis.product.dto;

public record UpdateProductRequestDto(
    String name,
    Integer price,
    String brandName
) {

}
