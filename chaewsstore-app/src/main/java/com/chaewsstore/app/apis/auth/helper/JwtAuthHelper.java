package com.chaewsstore.app.apis.auth.helper;

import static com.chaewsstore.app.common.exception.ExceptionConstants.NOT_FOUND_REFRESH_TOKEN;
import static com.chaewsstore.app.common.exception.ExceptionConstants.WITHOUT_OWNERSHIP_REFRESH_TOKEN;
import static com.chaewsstore.core.infra.jwt.AuthConstants.BEARER_TYPE;

import com.chaewsstore.core.domain.refresh.RefreshToken;
import com.chaewsstore.core.domain.refresh.RefreshTokenService;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.infra.jwt.Jwts;
import com.chaewsstore.core.infra.jwt.TokenProvider;
import com.globalutils.annotation.Helper;
import com.globalutils.exception.NotFoundException;
import com.globalutils.exception.UnauthorizedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RequiredArgsConstructor
@Helper
public class JwtAuthHelper {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 새로운 액세스 토큰과 리프레시 토큰을 생성 및 저장
     *
     * @param user           토큰을 생성할 사용자
     * @param authentication 인증 정보
     * @return 생성된 액세스 토큰과 리프레시 토큰
     */
    public Jwts generateTokensAndSave(User user, Authentication authentication) {
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken();
        refreshTokenService.create(RefreshToken.create(user, refreshToken));

        return Jwts.of(accessToken, refreshToken, BEARER_TYPE);
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰 재발급
     *
     * @param user         토큰을 재발급할 사용자
     * @param refreshToken 기존 리프레시 토큰
     * @return 새로운 액세스 토큰과 리프레시 토큰
     */
    public Jwts reissueToken(User user, String refreshToken) {
        validateAndDeleteRefreshToken(user, refreshToken);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getUsername(), null,
            List.of(new SimpleGrantedAuthority(user.getRole().getKey())));
        return generateTokensAndSave(user, authentication);
    }

    /**
     * 주어진 토큰에서 주체(subject) 추출
     *
     * @param token JWT 토큰
     * @return 토큰에서 추출된 주체(subject)
     */
    public String getSubject(String token) {
        return tokenProvider.getSubjectFromToken(token);
    }

    /**
     * 리프레시 토큰 삭제
     *
     * @param user         리프레시 토큰을 삭제할 사용자
     * @param refreshToken 삭제할 리프레시 토큰
     * @throws NotFoundException     리프레시 토큰이 존재하지 않는 경우
     * @throws UnauthorizedException 리프레시 토큰이 사용자의 토큰과 일치하지 않는 경우
     */
    public void removeRefreshToken(User user, String refreshToken) {
        validateAndDeleteRefreshToken(user, refreshToken);
    }

    /**
     * 리프레시 토큰 검증 및 삭제
     *
     * @param user         검증할 사용자
     * @param refreshToken 검증할 리프레시 토큰
     * @throws NotFoundException     리프레시 토큰이 존재하지 않는 경우
     * @throws UnauthorizedException 리프레시 토큰이 사용자의 토큰과 일치하지 않는 경우
     */
    private void validateAndDeleteRefreshToken(User user, String refreshToken) {
        RefreshToken matchRefreshToken = refreshTokenService.readByToken(refreshToken)
            .orElseThrow(() -> NOT_FOUND_REFRESH_TOKEN);

        if (!matchRefreshToken.getUser().equals(user)) {
            throw WITHOUT_OWNERSHIP_REFRESH_TOKEN;
        }

        refreshTokenService.remove(matchRefreshToken);
    }
}
