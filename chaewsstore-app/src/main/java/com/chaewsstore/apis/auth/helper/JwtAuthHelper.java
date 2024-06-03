package com.chaewsstore.apis.auth.helper;

import static com.chaewsstore.common.util.AuthConstants.BEARER_TYPE;

import com.chaewsstore.common.security.jwt.Jwts;
import com.chaewsstore.common.security.jwt.TokenProvider;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.refresh.RefreshToken;
import com.chaewsstore.core.domain.refresh.RefreshTokenService;
import com.globalutils.annotation.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
@Helper
public class JwtAuthHelper {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 새로운 액세스 토큰과 리프레시 토큰을 생성 및 저장
     *
     * @param account        토큰을 생성할 사용자
     * @param authentication 인증 정보
     * @return 생성된 액세스 토큰과 리프레시 토큰
     */
    public Jwts generateTokensAndSave(Account account, Authentication authentication) {
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken();
        refreshTokenService.create(RefreshToken.create(account, refreshToken));

        return Jwts.of(accessToken, refreshToken, BEARER_TYPE);
    }
}
