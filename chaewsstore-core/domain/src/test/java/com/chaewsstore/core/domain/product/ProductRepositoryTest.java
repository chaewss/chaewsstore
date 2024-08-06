package com.chaewsstore.core.domain.product;

import static com.chaewsstore.core.domain.BrandFixture.BRAND1;
import static com.chaewsstore.core.domain.ProductFixture.PRODUCT1;
import static com.chaewsstore.core.domain.ProductFixture.PRODUCT2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chaewsstore.core.domain.brand.Brand;
import com.chaewsstore.core.domain.config.TestConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20);

        Brand brand = entityManager.merge(BRAND1.getBrand());
        product1 = entityManager.merge(PRODUCT1.getProductWithBrand(brand));
        product2 = entityManager.merge(PRODUCT2.getProductWithBrand(brand));
    }

    @Test
    @DisplayName("최근 생성된 순으로 모든 상품을 조회한다")
    void succeed_to_find_all_products_order_by_created_at_desc() {
        Slice<Product> products = productRepository.findAllByOrderByCreatedAtDesc(pageable);

        assertThat(products.getContent()).hasSize(2);
        assertThat(products.getContent().get(0)).isEqualTo(product2);
        assertThat(products.getContent().get(1)).isEqualTo(product1);
    }

    @Test
    @DisplayName("이름을 통해 상품 존재 여부를 확인한다")
    void check_product_exists_by_name() {
        boolean exists = productRepository.existsByName(product1.getName());
        boolean notExists = productRepository.existsByName("product999");

        assertTrue(exists);
        assertFalse(notExists);
    }

    Product product1;
    Product product2;
    Pageable pageable;
}
