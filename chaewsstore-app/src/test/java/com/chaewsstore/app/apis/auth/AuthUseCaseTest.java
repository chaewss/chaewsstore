package com.chaewsstore.app.apis.auth;

import static com.chaewsstore.core.infra.jwt.AuthConstants.BEARER_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.chaewsstore.app.apis.auth.dto.LoginRequestDto;
import com.chaewsstore.app.apis.auth.dto.LoginResponseDto;
import com.chaewsstore.app.apis.auth.dto.LogoutRequestDto;
import com.chaewsstore.app.apis.auth.dto.ReissueTokenRequestDto;
import com.chaewsstore.app.apis.auth.dto.ReissueTokenResponseDto;
import com.chaewsstore.app.apis.auth.helper.JwtAuthHelper;
import com.chaewsstore.app.apis.auth.usecase.AuthUseCase;
import com.chaewsstore.app.common.helper.PasswordEncoderHelper;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.user.UserErrorCode;
import com.chaewsstore.core.domain.user.UserService;
import com.chaewsstore.core.domain.user.Role;
import com.chaewsstore.core.infra.jwt.Jwts;
import com.globalutils.exception.NotFoundException;
import com.globalutils.exception.UnauthorizedException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @InjectMocks
    private AuthUseCase authUseCase;

    @Mock
    private PasswordEncoderHelper passwordEncoderHelper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtAuthHelper jwtAuthHelper;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("로그인에 성공하면 토큰을 얻는다")
    void succeed_to_login() {
        LoginRequestDto requestDto = new LoginRequestDto("email@gmail.com", "password1!");
        Authentication authentication = mock(Authentication.class);

        given(userService.readByUsername(requestDto.email())).willReturn(Optional.of(user));
        given(passwordEncoderHelper.matches(requestDto.password(), user.getPassword())).willReturn(
            true);

        given(authenticationManager.authenticate(any())).willReturn(authentication);

        given(jwtAuthHelper.generateTokensAndSave(any(), any())).willReturn(token);

        LoginResponseDto responseDto = authUseCase.login(requestDto);

        assertThat(responseDto.token().accessToken()).isEqualTo(accessToken);
        assertThat(responseDto.token().refreshToken()).isEqualTo(refreshToken);
        then(userService).should(times(1)).readByUsername(any());
        then(passwordEncoderHelper).should(times(1)).matches(any(), any());
        then(jwtAuthHelper).should(times(1)).generateTokensAndSave(any(), any());
    }

    @Test
    @DisplayName("해당하는 아이디를 가진 사용자가 없으면 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_user_tries_to_login_but_user_does_not_exist() {
        LoginRequestDto request = new LoginRequestDto("whoareyou@gmail.com", "password1!");

        given(userService.readByUsername(any())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authUseCase.login(request));

        then(userService).should(times(1)).readByUsername(any());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 UnauthorizedException이 발생한다")
    void should_throw_UnauthorizedException_when_password_is_not_correct() {
        LoginRequestDto request = new LoginRequestDto("email@gmail.com", "incorrectPassword!");

        given(userService.readByUsername(any())).willReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> authUseCase.login(request));

        then(userService).should(times(1)).readByUsername(any());
    }

    @Test
    @DisplayName("토큰 재발급에 성공한다")
    void succeed_to_reissue_token() {
        ReissueTokenRequestDto request = new ReissueTokenRequestDto("access_token",
            "refresh_token");

        given(jwtAuthHelper.getSubject(request.accessToken())).willReturn(
            user.getUsername());
        given(userService.readByUsername(any())).willReturn(Optional.of(user));

        given(jwtAuthHelper.reissueToken(any(), any())).willReturn(token);

        ReissueTokenResponseDto response = authUseCase.reissueToken(request);

        assertThat(response.token().accessToken()).isEqualTo(accessToken);
        assertThat(response.token().refreshToken()).isEqualTo(refreshToken);
        then(jwtAuthHelper).should(times(1)).getSubject(any());
        then(userService).should(times(1)).readByUsername(any());
        then(jwtAuthHelper).should(times(1)).reissueToken(any(), any());
    }

    @Test
    @DisplayName("사용자 계정이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_reissue_token_but_user_does_not_exist() {
        ReissueTokenRequestDto request = new ReissueTokenRequestDto("access_token",
            "refresh_token");

        given(jwtAuthHelper.getSubject(request.accessToken())).willReturn(
            user.getUsername());
        given(userService.readByUsername(any())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> authUseCase.reissueToken(request));

        then(jwtAuthHelper).should(times(1)).getSubject(any());
        then(userService).should(times(1)).readByUsername(any());
        assertEquals(UserErrorCode.NOT_FOUND_USER, result.getResponseCode());
    }

    @Test
    @DisplayName("로그아웃에 성공한다")
    void succeed_to_logout() {
        LogoutRequestDto request = new LogoutRequestDto(refreshToken);

        willDoNothing().given(jwtAuthHelper).removeRefreshToken(any(), any());

        authUseCase.logout(user, request);

        then(jwtAuthHelper).should(times(1)).removeRefreshToken(any(), any());
    }

    User user = User.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("password1!")
        .nickname("nickname")
        .role(Role.ASSOCIATE)
        .build();

    String accessToken = "Bearer (accessToken)";
    String refreshToken = "(refreshToken)";
    Jwts token = Jwts.of(accessToken, refreshToken, BEARER_TYPE);
}
