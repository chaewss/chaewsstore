package com.chaewsstore.core.domain.adminRefresh;

import com.globalutils.annotation.DomainService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@DomainService
public class AdminRefreshTokenService {

    private final AdminRefreshTokenRepository adminRefreshTokenRepository;

    public void create(AdminRefreshToken adminRefreshToken) {
        adminRefreshTokenRepository.save(adminRefreshToken);
    }
}
