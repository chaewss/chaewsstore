package com.chaewsstore.apis.product.usecase;

import com.chaewsstore.apis.product.dto.ReadProductResponseDto;
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
}
