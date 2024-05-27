package com.chaewsstore.app.apis.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.chaewsstore.apis.auth.dto.LoginRequestDto;
import com.chaewsstore.apis.auth.dto.LoginResponseDto;
import com.chaewsstore.apis.auth.service.AuthService;
import com.chaewsstore.common.security.jwt.TokenProvider;
import com.chaewsstore.core.common.exception.NotFoundException;
import com.chaewsstore.core.common.exception.UnauthorizedException;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.AccountRepository;
import com.chaewsstore.core.domain.account.Role;
import com.chaewsstore.core.domain.refresh.RefreshTokenRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("로그인에 성공하면 토큰을 얻는다")
    void should_get_tokens_when_succeed_to_login() {
        LoginRequestDto requestDto = new LoginRequestDto("email@gmail.com", "password1!");
        Authentication authentication = mock(Authentication.class);

        given(accountRepository.findByUsername(requestDto.email())).willReturn(
            Optional.of(account));
        given(passwordEncoder.matches(requestDto.password(), account.getPassword())).willReturn(
            true);

        given(authenticationManager.authenticate(any())).willReturn(authentication);

        String accessToken = "Bearer (accessToken)";
        String refreshToken = "(refreshToken)";
        given(tokenProvider.generateAccessToken(any())).willReturn(accessToken);
        given(tokenProvider.generateRefreshToken()).willReturn(refreshToken);

        LoginResponseDto responseDto = authService.login(requestDto);

        assertThat(responseDto.token().accessToken()).isEqualTo(accessToken);
        assertThat(responseDto.token().refreshToken()).isEqualTo(refreshToken);
        then(accountRepository).should(times(1)).findByUsername(any());
        then(refreshTokenRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("해당하는 아이디를 가진 사용자가 없으면 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_user_tries_to_login_but_user_does_not_exist() {
        LoginRequestDto request = new LoginRequestDto("whoareyou@gmail.com", "password1!");

        given(accountRepository.findByUsername(any())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.login(request));

        then(accountRepository).should(times(1)).findByUsername(any());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 UnauthorizedException이 발생한다")
    void should_throw_UnauthorizedException_when_password_is_not_correct() {
        LoginRequestDto request = new LoginRequestDto("email@gmail.com", "incorrectPassword!");

        given(accountRepository.findByUsername(any())).willReturn(
            Optional.of(account));

        assertThrows(UnauthorizedException.class, () -> authService.login(request));

        then(accountRepository).should(times(1)).findByUsername(any());
    }

    Account account = Account.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("password1!")
        .nickname("nickname")
        .role(Role.ASSOCIATE)
        .build();
}
