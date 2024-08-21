package com.chaewsstore.core.domain.brand;

import static com.chaewsstore.core.domain.BrandFixture.BRAND1;
import static com.chaewsstore.core.domain.BrandFixture.BRAND2;
import static org.assertj.core.api.Assertions.assertThat;

import com.chaewsstore.core.domain.config.TestConfig;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DisplayName("BrandRepository 클래스")
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        brand1 = entityManager.merge(BRAND1.getBrand());
        brand2 = entityManager.merge(BRAND2.getBrand());
    }

    @Test
    @DisplayName("브랜드명을 통해 브랜드를 조회한다")
    void succeed_to_find_brand_by_name() {
        Optional<Brand> foundBrand1 = brandRepository.findByName(brand1.getName());
        Optional<Brand> foundBrand2 = brandRepository.findByName(brand2.getName());

        assertThat(foundBrand1).contains(brand1);
        assertThat(foundBrand2).contains(brand2);
    }

    Brand brand1;
    Brand brand2;
}
