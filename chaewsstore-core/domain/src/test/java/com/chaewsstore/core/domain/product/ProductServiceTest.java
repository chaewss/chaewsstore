package com.chaewsstore.core.domain.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        product1 = Product.builder().build();
        product2 = Product.builder().build();
    }

    @Test
    @DisplayName("상품을 생성한다")
    void shouldCreateProduct() {
        given(productRepository.save(product1)).willReturn(product1);

        Product result = productService.create(product1);

        then(productRepository).should(times(1)).save(product1);
        assertEquals(product1, result);
    }

    @Test
    @DisplayName("모든 상품을 페이지네이션으로 조회한다")
    void shouldReadAllProducts() {
        Pageable pageable = PageRequest.of(0, 20);
        Slice<Product> productSlice = new PageImpl<>(List.of(product1, product2));

        given(productRepository.findAllByOrderByCreatedAtDesc(pageable)).willReturn(productSlice);

        Slice<Product> result = productService.readAll(pageable);

        then(productRepository).should(times(1)).findAllByOrderByCreatedAtDesc(pageable);
        assertEquals(productSlice, result);
    }

    @Test
    @DisplayName("아이디로 상품을 조회한다")
    void should_read_product_by_id() {
        Long productId = 1L;
        given(productRepository.findById(productId)).willReturn(Optional.of(product1));

        Optional<Product> result = productService.readById(productId);

        then(productRepository).should(times(1)).findById(productId);
        assertTrue(result.isPresent());
        assertEquals(product1, result.get());
    }

    @Test
    @DisplayName("상품명으로 상품 존재 여부를 확인한다")
    void should_check_if_product_exists_by_name() {
        String name = "상품명";
        given(productRepository.existsByName(name)).willReturn(true);

        boolean result = productService.existsByName(name);

        then(productRepository).should(times(1)).existsByName(name);
        assertTrue(result);
    }

    @Test
    @DisplayName("상품을 삭제한다")
    void should_remove_product() {
        productService.remove(product1);
        then(productRepository).should(times(1)).delete(product1);
    }

    private Product product1;
    private Product product2;
}
