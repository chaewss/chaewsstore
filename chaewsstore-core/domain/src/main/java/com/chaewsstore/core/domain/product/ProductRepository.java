package com.chaewsstore.core.domain.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.brand ORDER BY p.createdAt DESC")
    Slice<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByName(String name);
}
