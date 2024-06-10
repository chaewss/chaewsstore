package com.chaewsstore.apis.product.controller;

import com.chaewsstore.apis.product.dto.CreateProductRequestDto;
import com.chaewsstore.apis.product.dto.ReadProductResponseDto;
import com.chaewsstore.apis.product.dto.UpdateProductRequestDto;
import com.chaewsstore.apis.product.usecase.ProductUseCase;
import com.globalutils.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    @GetMapping()
    public SuccessResponse<Slice<ReadProductResponseDto>> readProducts(Pageable pageable) {
        return SuccessResponse.from(productUseCase.readProductList(pageable));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> createProduct(@RequestBody CreateProductRequestDto request) {
        productUseCase.createProduct(request);
        return SuccessResponse.create();
    }

    @PutMapping("/{productId}")
    public SuccessResponse<Void> updateProduct(@PathVariable Long productId,
        @RequestBody UpdateProductRequestDto request) {
        productUseCase.updateProduct(productId, request);
        return SuccessResponse.create();
    }

    @DeleteMapping("/{productId}")
    public SuccessResponse<Void> deleteProduct(@PathVariable Long productId) {
        productUseCase.deleteProduct(productId);
        return SuccessResponse.create();
    }
}
