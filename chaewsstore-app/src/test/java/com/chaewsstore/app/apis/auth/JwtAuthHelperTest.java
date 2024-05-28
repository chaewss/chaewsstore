package com.chaewsstore.app.apis.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.chaewsstore.apis.auth.helper.JwtAuthHelper;
import com.chaewsstore.common.security.jwt.Jwts;
import com.chaewsstore.common.security.jwt.TokenProvider;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.Role;
import com.chaewsstore.core.domain.refresh.RefreshToken;
import com.chaewsstore.core.domain.refresh.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class JwtAuthHelperTest {

    @InjectMocks
    private JwtAuthHelper jwtAuthHelper;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("토큰을 생성하고 저장한다")
    void should_get_tokens_when_succeed_to_login() {
        // Arrange
        String expectedAccessToken = "access_token";
        String expectedRefreshToken = "refresh_token";
        String bearerType = "Bearer";
        Authentication authentication = mock(Authentication.class);

        given(tokenProvider.generateAccessToken(authentication)).willReturn(expectedAccessToken);
        given(tokenProvider.generateRefreshToken()).willReturn(expectedRefreshToken);

        Jwts result = jwtAuthHelper.generateTokensAndSave(account, authentication);

        assertEquals(expectedAccessToken, result.accessToken());
        assertEquals(expectedRefreshToken, result.refreshToken());
        assertEquals(bearerType, result.grantType());
        then(refreshTokenService).should(times(1)).create(any(RefreshToken.class));
    }

    Account account = Account.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("password1!")
        .nickname("nickname")
        .role(Role.ASSOCIATE)
        .build();
}
