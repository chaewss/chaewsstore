package com.chaewsstore.core.domain.product;

import com.globalutils.annotation.DomainService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@DomainService
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Slice<Product> readAll(Pageable pageable) {
        return productRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Product> readById(Long id) {
        return productRepository.findById(id);
    }
}
