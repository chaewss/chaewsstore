package com.chaewsstore.core.domain.refresh;

import com.globalutils.annotation.DomainService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@DomainService
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void create(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> readByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void remove(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
