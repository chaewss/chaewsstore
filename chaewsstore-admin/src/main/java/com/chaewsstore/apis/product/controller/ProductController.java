package com.chaewsstore.apis.product.controller;

import com.chaewsstore.apis.product.dto.ReadProductResponseDto;
import com.chaewsstore.apis.product.usecase.ProductUseCase;
import com.chaewsstore.common.response.ResponseCode;
import com.chaewsstore.common.response.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    @GetMapping()
    public ResponseData<Slice<ReadProductResponseDto>> readProducts(Pageable pageable) {
        return ResponseData.of(ResponseCode.READ_PRODUCTS_SUCCESS,
            productUseCase.readProductList(pageable));
    }
}
