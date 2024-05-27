package com.chaewsstore.apis.auth.service;

import static com.chaewsstore.core.common.exception.ExceptionConstants.INVALID_PASSWORD;
import static com.chaewsstore.core.common.exception.ExceptionConstants.NOT_FOUND_ACCOUNT;
import static com.chaewsstore.common.security.AuthConstants.BEARER_TYPE;

import com.chaewsstore.apis.auth.dto.LoginRequestDto;
import com.chaewsstore.apis.auth.dto.LoginResponseDto;
import com.chaewsstore.common.security.jwt.Jwts;
import com.chaewsstore.common.security.jwt.TokenProvider;
import com.chaewsstore.core.common.exception.NotFoundException;
import com.chaewsstore.core.common.exception.UnauthorizedException;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.AccountRepository;
import com.chaewsstore.core.domain.refresh.RefreshToken;
import com.chaewsstore.core.domain.refresh.RefreshTokenRepository;
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
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

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
        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw INVALID_PASSWORD;
        }

        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken();
        refreshTokenRepository.save(RefreshToken.create(account, refreshToken));
        Jwts token = Jwts.of(accessToken, refreshToken, BEARER_TYPE);

        return new LoginResponseDto(account.getId(), token);
    }
}
