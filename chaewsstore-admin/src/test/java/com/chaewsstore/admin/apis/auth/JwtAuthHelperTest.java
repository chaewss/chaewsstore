package com.chaewsstore.admin.apis.auth;

import static com.chaewsstore.core.infra.jwt.AuthConstants.BEARER_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.chaewsstore.admin.apis.auth.helper.JwtAuthHelper;
import com.chaewsstore.core.domain.admin.Admin;
import com.chaewsstore.core.domain.adminRefresh.AdminRefreshToken;
import com.chaewsstore.core.domain.adminRefresh.AdminRefreshTokenErrorCode;
import com.chaewsstore.core.domain.adminRefresh.AdminRefreshTokenService;
import com.chaewsstore.core.infra.jwt.Jwts;
import com.chaewsstore.core.infra.jwt.TokenProvider;
import com.globalutils.exception.NotFoundException;
import com.globalutils.exception.UnauthorizedException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@DisplayName("JwtAuthHelper 클래스")
@ExtendWith(MockitoExtension.class)
class JwtAuthHelperTest {

    @InjectMocks
    private JwtAuthHelper jwtAuthHelper;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private AdminRefreshTokenService refreshTokenService;

    @Nested
    @DisplayName("generateTokensAndSave 메서드는")
    class generate_tokens_and_save {

        @Test
        @DisplayName("새로운 토큰을 생성하고 저장하는데 성공하면 해당 토큰을 반환한다")
        void succeed_to_generate_tokens_and_save() {
            Authentication authentication = mock(Authentication.class);

            given(tokenProvider.generateAccessToken(authentication)).willReturn(accessToken);
            given(tokenProvider.generateRefreshToken()).willReturn(refreshTokenValue);

            Jwts result = jwtAuthHelper.generateTokensAndSave(admin, authentication);

            assertEquals(accessToken, result.accessToken());
            assertEquals(refreshTokenValue, result.refreshToken());
            assertEquals(bearerType, result.grantType());
            then(refreshTokenService).should(times(1)).create(any(AdminRefreshToken.class));
        }
    }

    @Nested
    @DisplayName("reissueToken 메서드는")
    class reissue_token {

        @Test
        @DisplayName("리프레시 토큰을 사용하여 새로운 토큰을 재발급하는데 성공하면 해당 토큰을 반환한다")
        void succeed_to_reissue_token() {
            AdminRefreshToken refreshToken = AdminRefreshToken.create(admin, refreshTokenValue);
            String newAccessToken = "new_access_token";
            String newRefreshTokenValue = "new_refresh_token";

            given(refreshTokenService.readByToken(any())).willReturn(Optional.of(refreshToken));
            given(tokenProvider.generateAccessToken(any())).willReturn(newAccessToken);
            given(tokenProvider.generateRefreshToken()).willReturn(newRefreshTokenValue);

            Jwts result = jwtAuthHelper.reissueToken(admin, refreshTokenValue);

            assertEquals(newAccessToken, result.accessToken());
            assertEquals(newRefreshTokenValue, result.refreshToken());
            assertEquals(bearerType, result.grantType());
            then(refreshTokenService).should(times(1)).readByToken(any());
            then(refreshTokenService).should(times(1)).remove(any(AdminRefreshToken.class));
            then(refreshTokenService).should(times(1)).create(any(AdminRefreshToken.class));
        }

        @Test
        @DisplayName("리프레시 토큰이 존재하지 않는 경우 NotFoundException이 발생한다")
        void should_throw_NotFoundException_when_reissue_token_but_refresh_token_does_not_exist() {
            given(refreshTokenService.readByToken(any())).willReturn(Optional.empty());

            NotFoundException result = assertThrows(NotFoundException.class,
                () -> jwtAuthHelper.reissueToken(admin, refreshTokenValue));

            then(refreshTokenService).should(times(1)).readByToken(any());
            assertEquals(AdminRefreshTokenErrorCode.NOT_FOUND_REFRESH_TOKEN,
                result.getResponseCode());
        }

        @Test
        @DisplayName("리프레시 토큰이 관리자의 토큰과 일치하지 않는 경우 UnauthorizedException이 발생한다")
        void should_throw_UnauthorizedException_when_reissue_token_but_admin_does_not_match() {
            AdminRefreshToken refreshToken = AdminRefreshToken.create(anotherAdmin,
                refreshTokenValue);

            given(refreshTokenService.readByToken(any())).willReturn(Optional.of(refreshToken));

            UnauthorizedException result = assertThrows(UnauthorizedException.class,
                () -> jwtAuthHelper.reissueToken(admin, refreshTokenValue));

            then(refreshTokenService).should(times(1)).readByToken(any());
            assertEquals(AdminRefreshTokenErrorCode.WITHOUT_OWNERSHIP_REFRESH_TOKEN,
                result.getResponseCode());
        }
    }

    @Nested
    @DisplayName("removeRefreshToken 메서드는")
    class remove_refresh_token {

        @Test
        @DisplayName("리프레시 토큰이 관리자의 토큰과 일치하는 경우 해당 토큰을 삭제한다")
        void succeed_to_remove_refresh_token() {
            AdminRefreshToken refreshToken = AdminRefreshToken.create(admin, refreshTokenValue);

            given(refreshTokenService.readByToken(any())).willReturn(Optional.of(refreshToken));
            willDoNothing().given(refreshTokenService).remove(any());

            jwtAuthHelper.removeRefreshToken(admin, refreshTokenValue);

            then(refreshTokenService).should(times(1)).readByToken(any());
            then(refreshTokenService).should(times(1)).remove(any(AdminRefreshToken.class));
        }

        @Test
        @DisplayName("리프레시 토큰이 존재하지 않는 경우 NotFoundException이 발생한다")
        void should_throw_NotFoundException_when_logout_but_refresh_token_does_not_exist() {
            given(refreshTokenService.readByToken(any())).willReturn(Optional.empty());

            NotFoundException result = assertThrows(NotFoundException.class,
                () -> jwtAuthHelper.removeRefreshToken(admin, refreshTokenValue));

            then(refreshTokenService).should(times(1)).readByToken(any());
            assertEquals(AdminRefreshTokenErrorCode.NOT_FOUND_REFRESH_TOKEN,
                result.getResponseCode());
        }

        @Test
        @DisplayName("리프레시 토큰이 관리자의 토큰과 일치하지 않는 경우 UnauthorizedException이 발생한다")
        void should_throw_UnauthorizedException_when_logout_but_admin_does_not_match() {
            AdminRefreshToken refreshToken = AdminRefreshToken.create(anotherAdmin,
                refreshTokenValue);

            given(refreshTokenService.readByToken(any())).willReturn(Optional.of(refreshToken));

            UnauthorizedException result = assertThrows(UnauthorizedException.class,
                () -> jwtAuthHelper.removeRefreshToken(admin, refreshTokenValue));

            then(refreshTokenService).should(times(1)).readByToken(any());
            assertEquals(AdminRefreshTokenErrorCode.WITHOUT_OWNERSHIP_REFRESH_TOKEN,
                result.getResponseCode());
        }
    }

    Admin admin = Admin.builder()
        .id(1L)
        .username("admin@gmail.com")
        .password("password1!")
        .name("어드민")
        .build();

    Admin anotherAdmin = Admin.builder()
        .id(2L)
        .username("anotherAdmin@gmail.com")
        .password("password1!")
        .name("어드민99")
        .build();

    String accessToken = "access_token";
    String refreshTokenValue = "refresh_token";
    String bearerType = BEARER_TYPE;
}
