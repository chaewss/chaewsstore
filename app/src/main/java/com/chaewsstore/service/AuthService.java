package com.chaewsstore.service;

import static com.chaewsstore.auth.AuthConstants.BEARER_TYPE;
import static com.chaewsstore.exception.ExceptionConstants.NOT_FOUND_ACCOUNT;

import com.chaewsstore.auth.TokenDto;
import com.chaewsstore.auth.TokenProvider;
import com.chaewsstore.dto.auth.LoginRequestDto;
import com.chaewsstore.dto.auth.LoginResponseDto;
import com.chaewsstore.entity.Account;
import com.chaewsstore.entity.RefreshToken;
import com.chaewsstore.exception.NotFoundException;
import com.chaewsstore.exception.UnauthorizedException;
import com.chaewsstore.repository.AccountRepository;
import com.chaewsstore.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * 로그인
     *
     * @param request 로그인 요청 정보
     * @return 사용자 id, 토큰 정보
     * @throws UnauthorizedException 비밀번호가 일치하지 않는 경우
     * @throws NotFoundException     해당하는 아이디를 가진 사용자가 없는 경우
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        Account account = accountRepository.findByUsername(request.email())
            .orElseThrow(() -> NOT_FOUND_ACCOUNT);
        account.validatePassword(request.password(), passwordEncoder);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        String accessToken = tokenProvider.createAccessToken(authentication);
        RefreshToken refreshToken = RefreshToken.create(account,
            tokenProvider.createRefreshToken());
        refreshTokenRepository.save(refreshToken);
        TokenDto token = TokenDto.of(accessToken, refreshToken.getToken(), BEARER_TYPE);

        return new LoginResponseDto(account.getId(), token);
    }
}
