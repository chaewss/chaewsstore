package com.chaewsstore.apis.product.usecase;

import static com.chaewsstore.common.exception.ExceptionConstants.DUPLICATE_PRODUCT;
import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_BRAND;

import com.chaewsstore.apis.product.dto.CreateProductRequestDto;
import com.chaewsstore.apis.product.dto.ReadProductResponseDto;
import com.chaewsstore.common.exception.DuplicateException;
import com.chaewsstore.common.exception.NotFoundException;
import com.chaewsstore.core.domain.brand.Brand;
import com.chaewsstore.core.domain.brand.BrandService;
import com.chaewsstore.core.domain.product.ProductService;
import com.globalutils.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class ProductUseCase {

    private final ProductService productService;
    private final BrandService brandService;

    /**
     * 상품 목록을 조회한다.
     *
     * @param pageable 페이지 정보
     * @return 상품 목록
     */
    @Transactional(readOnly = true)
    public Slice<ReadProductResponseDto> readProductList(Pageable pageable) {
        return productService.readAll(pageable).map(ReadProductResponseDto::of);
    }

    /**
     * 상품을 생성한다.
     *
     * @param request 생성할 상품에 대한 정보
     * @throws NotFoundException  브랜드가 존재하지 않는 경우
     * @throws DuplicateException 상품이 중복된 경우
     */
    @Transactional
    public void createProduct(CreateProductRequestDto request) {
        Brand brand = findBrandByName(request.brandName());
        if (productService.existsByName(request.name())) {
            throw DUPLICATE_PRODUCT;
        }
        productService.create(request.toEntity(brand));
    }

    private Brand findBrandByName(String brandName) {
        return brandService.readByName(brandName).orElseThrow(() -> NOT_FOUND_BRAND);
    }
}
