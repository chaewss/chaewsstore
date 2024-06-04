package com.chaewsstore.core.domain.brand;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);
}
