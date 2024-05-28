package com.chaewsstore.app.apis.auth;

import static com.chaewsstore.common.security.AuthConstants.BEARER_TYPE;
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
import com.chaewsstore.common.security.jwt.Jwts;
import com.chaewsstore.common.security.jwt.TokenProvider;
import com.chaewsstore.core.common.exception.NotFoundException;
import com.chaewsstore.core.common.exception.UnauthorizedException;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.AccountService;
import com.chaewsstore.core.domain.account.Role;
import com.chaewsstore.core.domain.refresh.RefreshTokenService;
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
class AuthUseCaseTest {

    @InjectMocks
    private AuthUseCase authUseCase;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtAuthHelper jwtAuthHelper;

    @Mock
    private AccountService accountService;

    @Test
    @DisplayName("로그인에 성공하면 토큰을 얻는다")
    void should_get_tokens_when_succeed_to_login() {
        LoginRequestDto requestDto = new LoginRequestDto("email@gmail.com", "password1!");
        Authentication authentication = mock(Authentication.class);

        given(accountService.readByUsername(requestDto.email())).willReturn(Optional.of(account));
        given(passwordEncoder.matches(requestDto.password(), account.getPassword())).willReturn(
            true);

        given(authenticationManager.authenticate(any())).willReturn(authentication);

        given(jwtAuthHelper.generateTokensAndSave(any(), any())).willReturn(token);

        LoginResponseDto responseDto = authUseCase.login(requestDto);

        assertThat(responseDto.token().accessToken()).isEqualTo(accessToken);
        assertThat(responseDto.token().refreshToken()).isEqualTo(refreshToken);
        then(accountService).should(times(1)).readByUsername(any());
        then(jwtAuthHelper).should(times(1)).generateTokensAndSave(any(), any());
    }

    @Test
    @DisplayName("해당하는 아이디를 가진 사용자가 없으면 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_user_tries_to_login_but_user_does_not_exist() {
        LoginRequestDto request = new LoginRequestDto("whoareyou@gmail.com", "password1!");

        given(accountService.readByUsername(any())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authUseCase.login(request));

        then(accountService).should(times(1)).readByUsername(any());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 UnauthorizedException이 발생한다")
    void should_throw_UnauthorizedException_when_password_is_not_correct() {
        LoginRequestDto request = new LoginRequestDto("email@gmail.com", "incorrectPassword!");

        given(accountService.readByUsername(any())).willReturn(Optional.of(account));

        assertThrows(UnauthorizedException.class, () -> authUseCase.login(request));

        then(accountService).should(times(1)).readByUsername(any());
    }

    Account account = Account.builder()
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
