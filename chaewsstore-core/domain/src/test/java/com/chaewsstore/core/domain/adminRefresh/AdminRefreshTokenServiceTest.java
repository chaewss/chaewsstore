package com.chaewsstore.core.domain.adminRefresh;

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

@DisplayName("AdminRefreshTokenService 클래스")
@ExtendWith(MockitoExtension.class)
class AdminRefreshTokenServiceTest {

    @Mock
    private AdminRefreshTokenRepository adminRefreshTokenRepository;

    @InjectMocks
    private AdminRefreshTokenService adminRefreshTokenService;

    @BeforeEach
    void setUp() {
        adminRefreshToken = new AdminRefreshToken();
    }

    @Test
    @DisplayName("관리자 리프레시 토큰을 생성한다")
    void should_create_admin_refresh_token() {
        adminRefreshTokenService.create(adminRefreshToken);
        then(adminRefreshTokenRepository).should(times(1)).save(adminRefreshToken);
    }

    @Test
    @DisplayName("토큰으로 관리자 리프레시 토큰을 조회한다")
    void should_read_admin_refresh_token_by_token() {
        String token = "testToken";
        given(adminRefreshTokenRepository.findByToken(token)).willReturn(Optional.of(adminRefreshToken));

        Optional<AdminRefreshToken> result = adminRefreshTokenService.readByToken(token);

        then(adminRefreshTokenRepository).should(times(1)).findByToken(token);
        assertTrue(result.isPresent());
        assertEquals(adminRefreshToken, result.get());
    }

    @Test
    @DisplayName("관리자 리프레시 토큰을 삭제한다")
    void should_remove_admin_refresh_token() {
        adminRefreshTokenService.remove(adminRefreshToken);
        then(adminRefreshTokenRepository).should(times(1)).delete(adminRefreshToken);
    }

    AdminRefreshToken adminRefreshToken;
}
