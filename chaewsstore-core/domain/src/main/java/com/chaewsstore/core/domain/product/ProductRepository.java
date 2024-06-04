package com.chaewsstore.core.domain.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProductRepository extends JpaRepository<Product, Long> {

    Slice<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
