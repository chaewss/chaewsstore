package com.chaewsstore.core.domain.refresh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("RefreshTokenService 클래스")
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshToken = new RefreshToken();
    }

    @Test
    @DisplayName("리프레시 토큰을 생성한다")
    void should_create_refresh_token() {
        refreshTokenService.create(refreshToken);
        then(refreshTokenRepository).should(times(1)).save(refreshToken);
    }

    @Test
    @DisplayName("토큰으로 리프레시 토큰을 조회한다")
    void should_read_refresh_token_by_token() {
        String token = "testToken";
        given(refreshTokenRepository.findByToken(token)).willReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = refreshTokenService.readByToken(token);

        then(refreshTokenRepository).should(times(1)).findByToken(token);
        assertTrue(result.isPresent());
        assertEquals(refreshToken, result.get());
    }

    @Test
    @DisplayName("리프레시 토큰을 삭제한다")
    void should_remove_refresh_token() {
        refreshTokenService.remove(refreshToken);
        then(refreshTokenRepository).should(times(1)).delete(refreshToken);
    }

    RefreshToken refreshToken;
}
