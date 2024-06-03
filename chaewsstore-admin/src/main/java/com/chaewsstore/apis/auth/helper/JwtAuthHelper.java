package com.chaewsstore.apis.auth.helper;

import static com.chaewsstore.common.util.AuthConstants.BEARER_TYPE;

import com.chaewsstore.common.security.jwt.Jwts;
import com.chaewsstore.common.security.jwt.TokenProvider;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.admin.Admin;
import com.chaewsstore.core.domain.adminRefresh.AdminRefreshToken;
import com.chaewsstore.core.domain.adminRefresh.AdminRefreshTokenService;
import com.chaewsstore.core.domain.refresh.RefreshToken;
import com.chaewsstore.core.domain.refresh.RefreshTokenService;
import com.globalutils.annotation.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
@Helper
public class JwtAuthHelper {

    private final TokenProvider tokenProvider;
    private final AdminRefreshTokenService refreshTokenService;

    public Jwts generateTokensAndSave(Admin admin, Authentication authentication) {
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken();
        refreshTokenService.create(AdminRefreshToken.create(admin, refreshToken));

        return Jwts.of(accessToken, refreshToken, BEARER_TYPE);
    }
}
