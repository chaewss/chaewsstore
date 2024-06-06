package com.chaewsstore.app.apis.auth;

import static com.chaewsstore.common.util.AuthConstants.BEARER_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.chaewsstore.apis.auth.dto.LoginRequestDto;
import com.chaewsstore.apis.auth.dto.LoginResponseDto;
import com.chaewsstore.apis.auth.dto.LogoutRequestDto;
import com.chaewsstore.apis.auth.dto.ReissueTokenRequestDto;
import com.chaewsstore.apis.auth.dto.ReissueTokenResponseDto;
import com.chaewsstore.apis.auth.helper.JwtAuthHelper;
import com.chaewsstore.apis.auth.usecase.AuthUseCase;
import com.chaewsstore.common.exception.NotFoundException;
import com.chaewsstore.common.exception.UnauthorizedException;
import com.chaewsstore.common.response.ResponseCode;
import com.chaewsstore.common.security.jwt.Jwts;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.AccountService;
import com.chaewsstore.core.domain.account.Role;
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
    void succeed_to_login() {
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

    @Test
    @DisplayName("토큰 재발급에 성공한다")
    void succeed_to_reissue_token() {
        ReissueTokenRequestDto request = new ReissueTokenRequestDto("access_token",
            "refresh_token");

        given(jwtAuthHelper.getSubjectFromToken(request.accessToken())).willReturn(
            account.getUsername());
        given(accountService.readByUsername(any())).willReturn(Optional.of(account));

        given(jwtAuthHelper.reissueToken(any(), any())).willReturn(token);

        ReissueTokenResponseDto response = authUseCase.reissueToken(request);

        assertThat(response.token().accessToken()).isEqualTo(accessToken);
        assertThat(response.token().refreshToken()).isEqualTo(refreshToken);
        then(jwtAuthHelper).should(times(1)).getSubjectFromToken(any());
        then(accountService).should(times(1)).readByUsername(any());
        then(jwtAuthHelper).should(times(1)).reissueToken(any(), any());
    }

    @Test
    @DisplayName("사용자 계정이 존재하지 않는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_reissue_token_but_account_does_not_exist() {
        ReissueTokenRequestDto request = new ReissueTokenRequestDto("access_token",
            "refresh_token");

        given(jwtAuthHelper.getSubjectFromToken(request.accessToken())).willReturn(
            account.getUsername());
        given(accountService.readByUsername(any())).willReturn(Optional.empty());

        NotFoundException result = assertThrows(NotFoundException.class,
            () -> authUseCase.reissueToken(request));

        then(jwtAuthHelper).should(times(1)).getSubjectFromToken(any());
        then(accountService).should(times(1)).readByUsername(any());
        assertEquals(ResponseCode.NOT_FOUND_ACCOUNT, result.getResponseCode());
    }

    @Test
    @DisplayName("로그아웃에 성공한다")
    void succeed_to_logout() {
        LogoutRequestDto request = new LogoutRequestDto(refreshToken);

        willDoNothing().given(jwtAuthHelper).removeRefreshToken(any(), any());

        authUseCase.logout(account, request);

        then(jwtAuthHelper).should(times(1)).removeRefreshToken(any(), any());
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
