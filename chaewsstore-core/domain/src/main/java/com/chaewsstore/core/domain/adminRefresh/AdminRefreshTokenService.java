package com.chaewsstore.core.domain.adminRefresh;

import com.globalutils.annotation.DomainService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@DomainService
public class AdminRefreshTokenService {

    private final AdminRefreshTokenRepository adminRefreshTokenRepository;

    @Transactional
    public void create(AdminRefreshToken adminRefreshToken) {
        adminRefreshTokenRepository.save(adminRefreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<AdminRefreshToken> readByToken(String token) {
        return adminRefreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void remove(AdminRefreshToken adminRefreshToken) {
        adminRefreshTokenRepository.delete(adminRefreshToken);
    }
}
