package com.chaewsstore.core.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.chaewsstore.core.domain.brand.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Product 클래스")
class ProductTest {

    @BeforeEach
    void setUp() {
        brand = Brand.builder()
            .id(1L)
            .name("Test Brand")
            .build();

        product = Product.create("상품1", 1000, brand);
    }

    @Test
    @DisplayName("create 메서드는 상품 생성 시, 초기 값들을 올바르게 설정한다")
    void should_create_product() {
        assertThat(product.getName()).isEqualTo("상품1");
        assertThat(product.getPrice()).isEqualTo(1000);
        assertThat(product.getBrand()).isEqualTo(brand);
        assertThat(product.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("updateProduct 메서드는 값들을 올바르게 변경한다")
    void should_update_product() {
        // given
        Brand newBrand = Brand.builder()
            .id(2L)
            .name("New Brand")
            .build();
        String newName = "상품987987";
        Integer newPrice = 2000;

        // when
        product.updateProduct(newName, newPrice, newBrand);

        // then
        assertThat(product.getName()).isEqualTo(newName);
        assertThat(product.getPrice()).isEqualTo(newPrice);
        assertThat(product.getBrand()).isEqualTo(newBrand);
    }

    private Product product;
    private Brand brand;
}
