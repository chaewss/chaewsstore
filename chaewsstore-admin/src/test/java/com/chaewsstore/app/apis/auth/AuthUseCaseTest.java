package com.chaewsstore.app.apis.auth;

import static com.chaewsstore.common.util.AuthConstants.BEARER_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.chaewsstore.apis.auth.dto.LoginRequestDto;
import com.chaewsstore.apis.auth.dto.LoginResponseDto;
import com.chaewsstore.apis.auth.helper.JwtAuthHelper;
import com.chaewsstore.apis.auth.usecase.AuthUseCase;
import com.chaewsstore.common.exception.NotFoundException;
import com.chaewsstore.common.exception.UnauthorizedException;
import com.chaewsstore.common.helper.PasswordEncoderHelper;
import com.chaewsstore.common.security.jwt.Jwts;
import com.chaewsstore.core.domain.admin.Admin;
import com.chaewsstore.core.domain.admin.AdminService;
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
    private AdminService adminService;

    @Test
    @DisplayName("로그인에 성공하면 토큰을 얻는다")
    void succeed_to_login() {
        LoginRequestDto requestDto = new LoginRequestDto("email@gmail.com", "password1!");
        Authentication authentication = mock(Authentication.class);

        given(adminService.readByUsername(requestDto.email())).willReturn(Optional.of(admin));
        given(passwordEncoderHelper.matches(requestDto.password(), admin.getPassword())).willReturn(
            true);

        given(authenticationManager.authenticate(any())).willReturn(authentication);

        given(jwtAuthHelper.generateTokensAndSave(any(), any())).willReturn(token);

        LoginResponseDto responseDto = authUseCase.login(requestDto);

        assertThat(responseDto.token().accessToken()).isEqualTo(accessToken);
        assertThat(responseDto.token().refreshToken()).isEqualTo(refreshToken);
        then(adminService).should(times(1)).readByUsername(any());
        then(passwordEncoderHelper).should(times(1)).matches(any(), any());
        then(authenticationManager).should(times(1)).authenticate(any());
        then(jwtAuthHelper).should(times(1)).generateTokensAndSave(any(), any());
    }

    @Test
    @DisplayName("해당하는 아이디를 가진 사용자가 없으면 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_user_tries_to_login_but_user_does_not_exist() {
        LoginRequestDto request = new LoginRequestDto("whoareyou@gmail.com", "password1!");

        given(adminService.readByUsername(any())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authUseCase.login(request));

        then(adminService).should(times(1)).readByUsername(any());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 UnauthorizedException이 발생한다")
    void should_throw_UnauthorizedException_when_password_is_not_correct() {
        LoginRequestDto request = new LoginRequestDto("email@gmail.com", "incorrectPassword!");

        given(adminService.readByUsername(any())).willReturn(Optional.of(admin));

        assertThrows(UnauthorizedException.class, () -> authUseCase.login(request));

        then(adminService).should(times(1)).readByUsername(any());
    }

    Admin admin = Admin.builder()
        .id(1L)
        .username("admin@gmail.com")
        .password("password1!")
        .name("어드민")
        .build();

    String accessToken = "Bearer (accessToken)";
    String refreshToken = "(refreshToken)";
    Jwts token = Jwts.of(accessToken, refreshToken, BEARER_TYPE);
}
