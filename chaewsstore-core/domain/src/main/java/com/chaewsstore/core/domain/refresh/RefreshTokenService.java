package com.chaewsstore.core.domain.refresh;

import com.globalutils.annotation.DomainService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@DomainService
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }
}
