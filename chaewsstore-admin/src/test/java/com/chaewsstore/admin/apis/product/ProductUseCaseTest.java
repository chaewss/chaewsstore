package com.chaewsstore.admin.apis.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.chaewsstore.apis.product.dto.CreateProductRequestDto;
import com.chaewsstore.apis.product.dto.ReadProductResponseDto;
import com.chaewsstore.apis.product.dto.UpdateProductRequestDto;
import com.chaewsstore.apis.product.usecase.ProductUseCase;
import com.chaewsstore.common.response.ErrorCode;
import com.chaewsstore.core.domain.brand.Brand;
import com.chaewsstore.core.domain.brand.BrandService;
import com.chaewsstore.core.domain.product.Product;
import com.chaewsstore.core.domain.product.ProductService;
import com.globalutils.exception.DuplicateException;
import com.globalutils.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
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

    @Mock
    private BrandService brandService;

    @Test
    @DisplayName("상품 목록을 조회한다")
    void succeed_to_read_product_list() {
        Page<Product> products = new PageImpl<>(List.of(product1, product2, product3));
        given(productService.readAll(any())).willReturn(products);

        Slice<ReadProductResponseDto> result = productUseCase.readProductList(any());

        assertEquals(products.getTotalElements(), result.getSize());
        then(productService).should(times(1)).readAll(any());
    }

    @Test
    @DisplayName("상품을 정상적으로 추가한다")
    void succeed_to_create_product() {
        CreateProductRequestDto request = new CreateProductRequestDto("새 상품", 80000, "Adidas");

        given(brandService.readByName(any())).willReturn(Optional.of(brand));
        given(productService.existsByName(any())).willReturn(false);
        given(productService.create(any())).willReturn(any());

        productUseCase.createProduct(request);

        then(brandService).should(times(1)).readByName(any());
        then(productService).should(times(1)).existsByName(any());
        then(productService).should(times(1)).create(any());
    }

    @Test
    @DisplayName("브랜드가 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_create_product_but_brand_does_not_exist() {
        CreateProductRequestDto request = new CreateProductRequestDto("새 상품", 80000, "Adidas");

        given(brandService.readByName(any())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> productUseCase.createProduct(request));

        then(brandService).should(times(1)).readByName(any());
        assertEquals(ErrorCode.NOT_FOUND_BRAND, result.getResponseCode());
    }

    @Test
    @DisplayName("해당 상품이 이미 존재하는 경우 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_product_but_product_is_duplicate() {
        CreateProductRequestDto request = new CreateProductRequestDto("헌 상품", 8000, "Adidas");

        given(brandService.readByName(any())).willReturn(Optional.of(brand));
        given(productService.existsByName(any())).willReturn(true);

        DuplicateException result = assertThrows(DuplicateException.class,
            () -> productUseCase.createProduct(request));

        then(brandService).should(times(1)).readByName(any());
        then(productService).should(times(1)).existsByName(any());
        assertEquals(ErrorCode.DUPLICATE_PRODUCT, result.getResponseCode());
    }

    @Test
    @DisplayName("상품을 정상적으로 수정한다")
    void succeed_to_update_product() {
        UpdateProductRequestDto request = new UpdateProductRequestDto("상품 11", 8000, "브렌드1");

        given(brandService.readByName(any())).willReturn(Optional.of(brand));
        given(productService.readById(anyLong())).willReturn(Optional.of(product1));
        given(productService.existsByName(any())).willReturn(false);

        productUseCase.updateProduct(1L, request);

        assertEquals(request.price(), product1.getPrice());
        then(brandService).should(times(1)).readByName(any());
        then(productService).should(times(1)).existsByName(any());
        then(productService).should(times(1)).readById(anyLong());
    }

    @Test
    @DisplayName("상품 수정 중 브랜드가 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_update_product_but_brand_does_not_exist() {
        UpdateProductRequestDto request = new UpdateProductRequestDto("상품 1", 600, "???");

        given(brandService.readByName(any())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> productUseCase.updateProduct(product1.getId(), request));

        then(brandService).should(times(1)).readByName(any());
        assertEquals(ErrorCode.NOT_FOUND_BRAND, result.getResponseCode());
    }

    @Test
    @DisplayName("상품 수정 중 상품이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_update_product_but_product_does_not_exist() {
        UpdateProductRequestDto request = new UpdateProductRequestDto("상품 11", 8000, "브렌드1");

        given(brandService.readByName(any())).willReturn(Optional.of(brand));
        given(productService.readById(anyLong())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> productUseCase.updateProduct(99L, request));

        then(brandService).should(times(1)).readByName(any());
        then(productService).should(times(1)).readById(any());
        assertEquals(ErrorCode.NOT_FOUND_PRODUCT, result.getResponseCode());
    }

    @Test
    @DisplayName("상품 수정 중 해당 상품이 이미 존재하는 경우 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_update_product_but_product_is_duplicate() {
        UpdateProductRequestDto request = new UpdateProductRequestDto("중복될 상품", 8000, "브렌드1");

        given(brandService.readByName(any())).willReturn(Optional.of(brand));
        given(productService.readById(anyLong())).willReturn(Optional.of(product1));
        given(productService.existsByName(any())).willReturn(true);

        DuplicateException result = assertThrows(DuplicateException.class,
            () -> productUseCase.updateProduct(product1.getId(), request));

        then(brandService).should(times(1)).readByName(any());
        then(productService).should(times(1)).readById(anyLong());
        then(productService).should(times(1)).existsByName(any());
        assertEquals(ErrorCode.DUPLICATE_PRODUCT, result.getResponseCode());
    }

    @Test
    @DisplayName("특정 상품을 정상적으로 삭제한다")
    void succeed_to_delete_product() {
        given(productService.readById(anyLong())).willReturn(Optional.of(product1));

        productUseCase.deleteProduct(1L);

        then(productService).should(times(1)).readById(anyLong());
        then(productService).should(times(1)).remove(any());
    }

    @Test
    @DisplayName("상품 삭제 중 상품이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFountException_when_delete_product_but_product_does_not_exist() {
        given(productService.readById(anyLong())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> productUseCase.deleteProduct(99L));

        then(productService).should(times(1)).readById(anyLong());
        assertEquals(ErrorCode.NOT_FOUND_PRODUCT, result.getResponseCode());
    }

    Brand brand = Brand.builder().name("브랜드1").build();
    Product product1 = Product.builder().id(1L).name("상품 1").price(600).brand(brand).build();
    Product product2 = Product.builder().name("중복될 상품").brand(brand).build();
    Product product3 = Product.builder().brand(brand).build();
}
