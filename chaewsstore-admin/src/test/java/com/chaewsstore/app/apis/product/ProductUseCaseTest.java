package com.chaewsstore.app.apis.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.chaewsstore.apis.product.dto.ReadProductResponseDto;
import com.chaewsstore.apis.product.usecase.ProductUseCase;
import com.chaewsstore.core.domain.brand.Brand;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.product.ProductService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Slice;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @InjectMocks
    private ProductUseCase productUseCase;

    @Mock
    private ProductService productService;

    @Test
    @DisplayName("상품 목록을 조회한다")
    void succeed_to_read_product_list() {
        Page<Product> products = new PageImpl<>(List.of(product1, product2, product3));
        given(productService.readAll(any())).willReturn(products);

        Slice<ReadProductResponseDto> result = productUseCase.readProductList(any());

        assertEquals(products.getTotalElements(), result.getSize());
        then(productService).should(times(1)).readAll(any());
    }

    Brand brand = Brand.builder().name("브랜드1").build();
    Product product1 = Product.builder().brand(brand).build();
    Product product2 = Product.builder().brand(brand).build();
    Product product3 = Product.builder().brand(brand).build();
}
