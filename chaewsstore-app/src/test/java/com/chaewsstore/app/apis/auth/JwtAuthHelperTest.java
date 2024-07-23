package com.chaewsstore.app.apis.auth;

import static com.chaewsstore.core.infra.jwt.AuthConstants.BEARER_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.chaewsstore.app.apis.auth.helper.JwtAuthHelper;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.user.Role;
import com.chaewsstore.core.domain.refresh.RefreshToken;
import com.chaewsstore.core.domain.refresh.RefreshTokenErrorCode;
import com.chaewsstore.core.domain.refresh.RefreshTokenService;
import com.chaewsstore.core.infra.jwt.Jwts;
import com.chaewsstore.core.infra.jwt.TokenProvider;
import com.globalutils.exception.NotFoundException;
import com.globalutils.exception.UnauthorizedException;
import java.util.Optional;
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
    void succeed_to_generate_tokens_and_save() {
        Authentication authentication = mock(Authentication.class);

        given(tokenProvider.generateAccessToken(authentication)).willReturn(accessToken);
        given(tokenProvider.generateRefreshToken()).willReturn(refreshTokenValue);

        Jwts result = jwtAuthHelper.generateTokensAndSave(user, authentication);

        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshTokenValue, result.refreshToken());
        assertEquals(bearerType, result.grantType());
        then(refreshTokenService).should(times(1)).create(any(RefreshToken.class));
    }

    @Test
    @DisplayName("토큰을 정상적으로 재발급한다")
    void succeed_to_reissue_token() {
        RefreshToken refreshToken = RefreshToken.create(user, refreshTokenValue);
        String newAccessToken = "new_access_token";
        String newRefreshTokenValue = "new_refresh_token";

        given(refreshTokenService.readByToken(any())).willReturn(Optional.of(refreshToken));
        given(tokenProvider.generateAccessToken(any())).willReturn(newAccessToken);
        given(tokenProvider.generateRefreshToken()).willReturn(newRefreshTokenValue);

        Jwts result = jwtAuthHelper.reissueToken(user, refreshTokenValue);

        assertEquals(newAccessToken, result.accessToken());
        assertEquals(newRefreshTokenValue, result.refreshToken());
        assertEquals(bearerType, result.grantType());
        then(refreshTokenService).should(times(1)).readByToken(any());
        then(refreshTokenService).should(times(1)).remove(any(RefreshToken.class));
        then(refreshTokenService).should(times(1)).create(any(RefreshToken.class));
    }

    @Test
    @DisplayName("리프레시 토큰이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_reissue_token_but_refresh_token_does_not_exist() {
        given(refreshTokenService.readByToken(any())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> jwtAuthHelper.reissueToken(user, refreshTokenValue));

        then(refreshTokenService).should(times(1)).readByToken(any());
        assertEquals(RefreshTokenErrorCode.NOT_FOUND_REFRESH_TOKEN, result.getResponseCode());
    }

    @Test
    @DisplayName("리프레시 토큰이 사용자의 토큰과 일치하지 않는 경우 UnauthorizedException이 발생한다")
    void should_throw_UnauthorizedException_when_reissue_token_but_user_does_not_match() {
        RefreshToken refreshToken = RefreshToken.create(anotherUser, refreshTokenValue);

        given(refreshTokenService.readByToken(any())).willReturn(Optional.of(refreshToken));

        UnauthorizedException result = assertThrows(UnauthorizedException.class,
            () -> jwtAuthHelper.reissueToken(user, refreshTokenValue));

        then(refreshTokenService).should(times(1)).readByToken(any());
        assertEquals(RefreshTokenErrorCode.WITHOUT_OWNERSHIP_REFRESH_TOKEN, result.getResponseCode());
    }

    @Test
    @DisplayName("토큰을 정상적으로 삭제한다")
    void succeed_to_remove_refresh_token() {
        RefreshToken refreshToken = RefreshToken.create(user, refreshTokenValue);

        given(refreshTokenService.readByToken(any())).willReturn(Optional.of(refreshToken));
        willDoNothing().given(refreshTokenService).remove(any());

        jwtAuthHelper.removeRefreshToken(user, refreshTokenValue);

        then(refreshTokenService).should(times(1)).readByToken(any());
        then(refreshTokenService).should(times(1)).remove(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그아웃 중 리프레시 토큰이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_logout_but_refresh_token_does_not_exist() {
        given(refreshTokenService.readByToken(any())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> jwtAuthHelper.removeRefreshToken(user, refreshTokenValue));

        then(refreshTokenService).should(times(1)).readByToken(any());
        assertEquals(RefreshTokenErrorCode.NOT_FOUND_REFRESH_TOKEN, result.getResponseCode());
    }

    @Test
    @DisplayName("로그아웃 중 리프레시 토큰이 관리자의 토큰과 일치하지 않는 경우 UnauthorizedException이 발생한다")
    void should_throw_UnauthorizedException_when_logout_but_admin_does_not_match() {
        RefreshToken refreshToken = RefreshToken.create(anotherUser, refreshTokenValue);

        given(refreshTokenService.readByToken(any())).willReturn(Optional.of(refreshToken));

        UnauthorizedException result = assertThrows(UnauthorizedException.class,
            () -> jwtAuthHelper.removeRefreshToken(user, refreshTokenValue));

        then(refreshTokenService).should(times(1)).readByToken(any());
        assertEquals(RefreshTokenErrorCode.WITHOUT_OWNERSHIP_REFRESH_TOKEN, result.getResponseCode());
    }

    User user = User.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("password1!")
        .nickname("nickname")
        .role(Role.ASSOCIATE)
        .build();

    User anotherUser = User.builder()
        .id(2L)
        .username("anotherEmail@gmail.com")
        .password("password1!")
        .nickname("nickname999")
        .role(Role.ASSOCIATE)
        .build();

    String accessToken = "access_token";
    String refreshTokenValue = "refresh_token";
    String bearerType = BEARER_TYPE;
}
