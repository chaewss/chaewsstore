package com.chaewsstore.core.domain.admin;

import com.globalutils.annotation.DomainService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@DomainService
public class AdminService {

    private final AdminRepository adminRepository;

    @Transactional
    public Admin create(Admin admin) {
        return adminRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public Optional<Admin> readByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Boolean existsByUsername(String username) {
        return adminRepository.existsByUsername(username);
    }
}
