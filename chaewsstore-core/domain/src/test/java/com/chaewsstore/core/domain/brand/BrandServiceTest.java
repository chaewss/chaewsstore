package com.chaewsstore.core.domain.brand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @BeforeEach
    void setUp() {
        brand = Brand.builder().build();
    }

    @Test
    @DisplayName("브랜드명으로 브랜드를 조회한다")
    void should_read_brand_by_name() {
        String name = "brandName";
        given(brandRepository.findByName(name)).willReturn(Optional.of(brand));

        Optional<Brand> result = brandService.readByName(name);

        then(brandRepository).should(times(1)).findByName(name);
        assertTrue(result.isPresent());
        assertEquals(brand, result.get());
    }

    Brand brand;
}
