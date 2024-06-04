package com.chaewsstore.core.domain.brand;

import com.globalutils.annotation.DomainService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@DomainService
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public Optional<Brand> readByName(String name) {
        return brandRepository.findByName(name);
    }
}
